package com.vault.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.entity.mysql.Chat;
import com.vault.entity.mysql.ChatFile;
import com.vault.entity.mysql.Message;
import com.vault.mapper.ChatFileMapper;
import com.vault.mapper.ChatMapper;
import com.vault.mapper.MessageMapper;
import com.vault.model.skill.RoutingDecision;
import com.vault.service.ai.*;
import com.vault.service.skill.*;
import com.vault.dto.request.SendMessageRequest;
import com.vault.dto.response.ChatResponse;
import com.vault.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天服务
 * 核心的智能问答逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final ChatFileMapper chatFileMapper;
    
    private final AIService aiService;
    private final RAGService ragService;
    private final SkillRouter skillRouter;
    private final SkillLoader skillLoader;
    private final SkillExecutor skillExecutor;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 发送消息并获取AI回复（同步）
     */
    @Transactional
    public MessageResponse sendMessage(Long userId, SendMessageRequest request) {
        log.info("User {} sends message in chat {}", userId, request.getChatId());
        
        try {
            // 1. 获取或创建会话
            log.debug("Step 1: Getting or creating chat");
            Chat chat = getOrCreateChat(userId, request.getChatId());
            log.debug("Chat ID: {}", chat.getId());
            
            // 2. 保存用户消息
            log.debug("Step 2: Saving user message");
            Message userMessage = saveUserMessage(chat.getId(), request);
            log.debug("User message ID: {}", userMessage.getId());
            
            // 3. 获取历史消息
            log.debug("Step 3: Getting history messages");
            List<Message> history = getRecentMessages(chat.getId(), 10);
            log.debug("History size: {}", history.size());
            
            // 4. RAG检索
            log.debug("Step 4: RAG search");
            RAGSearchResult ragResult = ragService.search(request.getContent(), 5);
            log.debug("RAG chunks: {}", ragResult.getChunks().size());
            
            // 5. 判断是否需要使用Skill
            log.debug("Step 5: Skill routing");
            SkillResult skillResult = null;
            RoutingDecision decision = skillRouter.route(request.getContent());
            if (decision != null && decision.getUseSkills() && !decision.getSelectedSkills().isEmpty()) {
                // 目前执行第一个技能（如需执行多个可循环）
                String skillName = decision.getSelectedSkills().get(0);
                SkillDefinition skill = skillLoader.getSkillByName(skillName);
                if (skill != null) {
                    skillResult = executeSkill(skill, userId, chat.getId(), request.getContent(), ragResult);
                }
            }
            log.debug("Skill result: {}", skillResult != null ? "executed" : "skipped");
            
            // 6. 构建AI请求
            log.debug("Step 6: Building AI messages");
            List<AIMessage> messages = buildAIMessages(request.getContent(), history, ragResult, skillResult);
            log.debug("AI messages count: {}", messages.size());
            
            // 7. 调用AI
            log.debug("Step 7: Calling AI service");
            AIRequest aiRequest = buildAIRequest(request);
            AIResponse aiResponse = aiService.chat(messages, aiRequest);
            log.debug("AI response length: {}", aiResponse.getContent().length());
            
            // 8. 保存AI回复
            log.debug("Step 8: Saving assistant message");
            Message assistantMessage = saveAssistantMessage(
                    chat.getId(), 
                    aiResponse.getContent(),
                    ragResult,
                    skillResult,
                    aiResponse.getUsage()
            );
            log.debug("Assistant message ID: {}", assistantMessage.getId());
            
            // 9. 更新会话信息
            log.debug("Step 9: Updating chat info");
            updateChatInfo(chat, aiResponse.getContent());
            
            // 10. 异步更新学生肖像
            log.debug("Step 10: Updating student profile async");
            updateStudentProfileAsync(userId, request.getContent(), ragResult);
            
            // 11. 构建响应
            log.debug("Step 11: Building response");
            return buildMessageResponse(assistantMessage, ragResult);
            
        } catch (Exception e) {
            log.error("Failed to process message at step. Error: {}, Cause: {}", 
                e.getClass().getName(), 
                e.getCause() != null ? e.getCause().getClass().getName() : "null", e);
            throw new RuntimeException("消息处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送消息并流式返回AI回复
     */
    public void sendMessageStream(Long userId, SendMessageRequest request, StreamCallback callback) {
        log.info("User {} sends streaming message in chat {}", userId, request.getChatId());
        
        try {
            // 前置处理与同步版本相同
            Chat chat = getOrCreateChat(userId, request.getChatId());
            Message userMessage = saveUserMessage(chat.getId(), request);
            List<Message> history = getRecentMessages(chat.getId(), 10);
            RAGSearchResult ragResult = ragService.search(request.getContent(), 5);
            
            SkillResult skillResult = null;
            RoutingDecision decision = skillRouter.route(request.getContent());
            if (decision != null && decision.getUseSkills() && !decision.getSelectedSkills().isEmpty()) {
                // 目前执行第一个技能（如需执行多个可循环）
                String skillName = decision.getSelectedSkills().get(0);
                SkillDefinition skill = skillLoader.getSkillByName(skillName);
                if (skill != null) {
                    skillResult = executeSkill(skill, userId, chat.getId(), request.getContent(), ragResult);
                }
            }
            
            List<AIMessage> messages = buildAIMessages(request.getContent(), history, ragResult, skillResult);
            AIRequest aiRequest = buildAIRequest(request);
            
            // 使用流式API
            final StringBuilder fullContent = new StringBuilder();
            final SkillResult finalSkillResult = skillResult;
            final Chat finalChat = chat;
            
            aiService.chatStream(messages, aiRequest, new StreamCallback() {
                @Override
                public void onNext(String content) {
                    fullContent.append(content);
                    callback.onNext(content);
                }
                
                @Override
                public void onComplete(AIResponse response) {
                    try {
                        // 保存AI回复
                        Message assistantMessage = saveAssistantMessage(
                                finalChat.getId(),
                                fullContent.toString(),
                                ragResult,
                                finalSkillResult,
                                response.getUsage()
                        );
                        
                        // 更新会话
                        updateChatInfo(finalChat, fullContent.toString());
                        
                        // 异步更新学生肖像
                        updateStudentProfileAsync(userId, request.getContent(), ragResult);
                        
                        callback.onComplete(response);
                        
                    } catch (Exception e) {
                        log.error("Failed to save streaming response", e);
                        callback.onError(e);
                    }
                }
                
                @Override
                public void onError(Throwable error) {
                    log.error("Streaming error", error);
                    callback.onError(error);
                }
            });
            
        } catch (Exception e) {
            log.error("Failed to process streaming message", e);
            callback.onError(e);
        }
    }
    
    /**
     * 获取或创建会话
     */
    private Chat getOrCreateChat(Long userId, Long chatId) {
        if (chatId != null) {
            Chat chat = chatMapper.selectById(chatId);
            if (chat != null && chat.getUserId().equals(userId)) {
                return chat;
            }
        }
        
        // 创建新会话
        Chat chat = new Chat();
        chat.setUserId(userId);
        chat.setTitle("新对话");
        chat.setStatus("active");
        chat.setMessageCount(0);
        chat.setPinned(false);
        chat.setLastMessageTime(LocalDateTime.now());
        chatMapper.insert(chat);
        
        log.info("Created new chat {} for user {}", chat.getId(), userId);
        return chat;
    }
    
    /**
     * 保存用户消息
     */
    private Message saveUserMessage(Long chatId, SendMessageRequest request) {
        Message message = new Message();
        message.setChatId(chatId);
        message.setRole("user");
        message.setContent(request.getContent());
        message.setStatus("sent");
        
        if (request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            try {
                message.setFileIds(objectMapper.writeValueAsString(request.getFileIds()));
            } catch (Exception e) {
                log.error("Failed to serialize file IDs", e);
            }
        }
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 保存AI回复消息
     */
    private Message saveAssistantMessage(Long chatId, String content, 
                                        RAGSearchResult ragResult,
                                        SkillResult skillResult,
                                        AIResponse.TokenUsage tokenUsage) {
        Message message = new Message();
        message.setChatId(chatId);
        message.setRole("assistant");
        message.setContent(content);
        message.setStatus("sent");
        
        // 保存引用信息
        if (ragResult != null && !ragResult.getChunks().isEmpty()) {
            try {
                List<Map<String, Object>> references = ragResult.getChunks().stream()
                        .map(chunk -> {
                            Map<String, Object> ref = new HashMap<>();
                            ref.put("materialId", chunk.getMaterialId());
                            ref.put("materialName", chunk.getMaterialName());
                            ref.put("chapter", chunk.getChapter());
                            ref.put("content", chunk.getContent().substring(0, Math.min(200, chunk.getContent().length())));
                            ref.put("score", chunk.getScore());
                            return ref;
                        })
                        .collect(Collectors.toList());
                message.setReferences(objectMapper.writeValueAsString(references));
                
                List<Long> materialIds = ragResult.getChunks().stream()
                        .map(RAGSearchResult.TextChunk::getMaterialId)
                        .distinct()
                        .collect(Collectors.toList());
                message.setMaterialIds(objectMapper.writeValueAsString(materialIds));
            } catch (Exception e) {
                log.error("Failed to serialize references", e);
            }
        }
        
        // 保存Skill信息
        if (skillResult != null) {
            message.setSkillUsed(skillResult.getSkillName());
        }
        
        // 保存Token使用情况
        if (tokenUsage != null) {
            try {
                message.setTokenUsage(objectMapper.writeValueAsString(tokenUsage));
            } catch (Exception e) {
                log.error("Failed to serialize token usage", e);
            }
        }
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 获取最近的消息
     */
    private List<Message> getRecentMessages(Long chatId, int limit) {
        return messageMapper.listRecentMessages(chatId, limit);
    }
    
    /**
     * 执行Skill
     */
    private SkillResult executeSkill(SkillDefinition skill, Long userId, Long chatId,
                                     String userInput, RAGSearchResult ragResult) {
        SkillContext context = new SkillContext();
        context.setUserId(userId);
        context.setChatId(chatId);
        context.setUserInput(userInput);
        context.setRagResults(ragResult);
        context.setVariables(new HashMap<>());
        
        return skillExecutor.execute(skill, context);
    }
    
    /**
     * 构建AI消息列表
     */
    private List<AIMessage> buildAIMessages(String userInput, List<Message> history,
                                           RAGSearchResult ragResult, SkillResult skillResult) {
        List<AIMessage> messages = new ArrayList<>();
        
        // 1. 系统提示词
        String systemPrompt = aiService.buildSystemPrompt();
        messages.add(AIMessage.system(systemPrompt));
        
        // 2. RAG上下文
        if (ragResult != null && !ragResult.getChunks().isEmpty()) {
            String context = ragService.buildContext(ragResult);
            messages.add(AIMessage.system(context));
        }
        
        // 3. Skill结果
        if (skillResult != null && skillResult.isSuccess()) {
            messages.add(AIMessage.system("Skill执行结果：\n" + skillResult.getOutput()));
        }
        
        // 4. 历史消息（最近N条）
        for (Message msg : history) {
            messages.add(new AIMessage(msg.getRole(), msg.getContent()));
        }
        
        // 5. 当前用户输入
        messages.add(AIMessage.user(userInput));
        
        return messages;
    }
    
    /**
     * 构建AI请求参数
     */
    private AIRequest buildAIRequest(SendMessageRequest request) {
        return AIRequest.builder()
                .model(request.getModel())
                .temperature(request.getTemperature())
                .maxTokens(request.getMaxTokens())
                .stream(request.getStream())
                .build();
    }
    
    /**
     * 更新会话信息
     */
    private void updateChatInfo(Chat chat, String lastMessage) {
        // 截取前100个字符作为预览
        String preview = lastMessage.length() > 100 ? 
                        lastMessage.substring(0, 100) + "..." : lastMessage;
        
        chat.setLastMessage(preview);
        chat.setLastMessageTime(LocalDateTime.now());
        chat.setMessageCount(chat.getMessageCount() + 2); // user + assistant
        
        // 如果是第一条消息，用问题作为标题
        if (chat.getMessageCount() == 2) {
            chat.setTitle(preview);
        }
        
        chatMapper.updateById(chat);
    }
    
    /**
     * 异步更新学生肖像
     */
    @Async
    public void updateStudentProfileAsync(Long userId, String question, RAGSearchResult ragResult) {
        try {
            // 1. 提取问题涉及的知识点
            List<String> knowledgePoints = extractKnowledgePoints(question, ragResult);
            
            // 2. 更新Neo4j中的学生-知识点关系
            // 这里需要调用Neo4j Repository
            log.info("Updated student profile for user {}, knowledge points: {}", 
                    userId, knowledgePoints);
            
        } catch (Exception e) {
            log.error("Failed to update student profile", e);
        }
    }
    
    /**
     * 提取知识点
     */
    private List<String> extractKnowledgePoints(String question, RAGSearchResult ragResult) {
        List<String> points = new ArrayList<>();
        
        // 简单实现：从RAG结果中提取
        if (ragResult != null) {
            for (RAGSearchResult.TextChunk chunk : ragResult.getChunks()) {
                if (chunk.getChapter() != null) {
                    points.add(chunk.getChapter());
                }
            }
        }
        
        return points.stream().distinct().collect(Collectors.toList());
    }
    
    /**
     * 构建消息响应
     */
    private MessageResponse buildMessageResponse(Message message, RAGSearchResult ragResult) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setRole(message.getRole());
        response.setContent(message.getContent());
        response.setSkillUsed(message.getSkillUsed());
        response.setCreateTime(message.getCreateTime());
        
        // 解析引用
        if (ragResult != null && !ragResult.getChunks().isEmpty()) {
            List<MessageResponse.MaterialReference> refs = ragResult.getChunks().stream()
                    .map(chunk -> MessageResponse.MaterialReference.builder()
                            .materialId(chunk.getMaterialId())
                            .materialName(chunk.getMaterialName())
                            .chapter(chunk.getChapter())
                            .content(chunk.getContent())
                            .relevance(chunk.getScore())
                            .build())
                    .collect(Collectors.toList());
            response.setReferences(refs);
        }
        
        return response;
    }
    
    /**
     * 获取用户的所有会话
     */
    public List<ChatResponse> listChats(Long userId) {
        List<Chat> chats = chatMapper.listByUserId(userId);
        return chats.stream()
                .map(this::convertToChatResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取会话详情（包含所有消息）
     */
    public ChatResponse getChatDetail(Long userId, Long chatId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !chat.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在");
        }
        
        List<Message> messages = messageMapper.listByChatId(chatId);
        
        ChatResponse response = convertToChatResponse(chat);
        response.setMessages(messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList()));
        
        return response;
    }
    
    /**
     * 删除会话
     */
    @Transactional
    public void deleteChat(Long userId, Long chatId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !chat.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在");
        }
        
        // 逻辑删除
        chatMapper.deleteById(chatId);
        
        // 删除所有消息
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getChatId, chatId);
        messageMapper.delete(wrapper);
        
        log.info("Deleted chat {} for user {}", chatId, userId);
    }
    
    private ChatResponse convertToChatResponse(Chat chat) {
        return ChatResponse.builder()
                .id(chat.getId())
                .title(chat.getTitle())
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getLastMessageTime())
                .messageCount(chat.getMessageCount())
                .pinned(chat.getPinned())
                .createTime(chat.getCreateTime())
                .build();
    }
    
    private MessageResponse convertToMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setRole(message.getRole());
        response.setContent(message.getContent());
        response.setSkillUsed(message.getSkillUsed());
        response.setCreateTime(message.getCreateTime());
        return response;
    }
}
