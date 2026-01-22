package com.vault.websocket;

import com.vault.dto.request.SendMessageRequest;
import com.vault.dto.response.MessageResponse;
import com.vault.security.JwtUtil;
import com.vault.service.ai.AIResponse;
import com.vault.service.ai.StreamCallback;
import com.vault.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket消息处理器
 * 处理实时聊天消息
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketHandler {
    
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;
    
    /**
     * 处理聊天消息
     * 客户端发送到: /app/chat/send
     * 服务端推送到: /user/{userId}/queue/messages
     */
    @MessageMapping("/chat/send")
    public void handleChatMessage(@Payload SendMessageRequest request,
                                  @Header("Authorization") String token,
                                  Principal principal) {
        try {
            // 从token获取用户ID
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            log.info("Received message from user {} via WebSocket", userId);
            
            // 发送"正在输入"状态
            sendTypingStatus(userId, true);
            
            // 处理消息（流式）
            chatService.sendMessageStream(userId, request, new StreamCallback() {
                
                @Override
                public void onNext(String content) {
                    // 发送流式内容块
                    Map<String, Object> chunk = new HashMap<>();
                    chunk.put("type", "chunk");
                    chunk.put("content", content);
                    
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(),
                            "/queue/messages",
                            chunk
                    );
                }
                
                @Override
                public void onComplete(AIResponse response) {
                    // 发送完成消息
                    Map<String, Object> complete = new HashMap<>();
                    complete.put("type", "complete");
                    complete.put("usage", response.getUsage());
                    
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(),
                            "/queue/messages",
                            complete
                    );
                    
                    // 停止"正在输入"状态
                    sendTypingStatus(userId, false);
                    
                    log.info("Message completed for user {}", userId);
                }
                
                @Override
                public void onError(Throwable error) {
                    // 发送错误消息
                    Map<String, Object> errorMsg = new HashMap<>();
                    errorMsg.put("type", "error");
                    errorMsg.put("message", error.getMessage());
                    
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(),
                            "/queue/messages",
                            errorMsg
                    );
                    
                    sendTypingStatus(userId, false);
                    
                    log.error("Error processing message for user {}", userId, error);
                }
            });
            
        } catch (Exception e) {
            log.error("Failed to handle WebSocket message", e);
        }
    }
    
    /**
     * 发送"正在输入"状态
     */
    private void sendTypingStatus(Long userId, boolean isTyping) {
        Map<String, Object> status = new HashMap<>();
        status.put("type", "typing");
        status.put("isTyping", isTyping);
        
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/status",
                status
        );
    }
    
    /**
     * 处理心跳消息
     */
    @MessageMapping("/chat/ping")
    public void handlePing(Principal principal) {
        // 返回pong
        Map<String, String> pong = new HashMap<>();
        pong.put("type", "pong");
        pong.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/status",
                pong
        );
    }
}
