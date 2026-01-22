package com.vault.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * AI服务 - 支持DeepSeek API (OpenAI兼容)
 * 负责调用外部LLM API
 */
@Slf4j
@Service
public class AIService {
    
    @Value("${vault.ai.api-key:}")
    private String apiKey;
    
    @Value("${vault.ai.api-url:https://api.deepseek.com/chat/completions}")
    private String apiUrl;
    
    @Value("${vault.ai.default-model:deepseek-chat}")
    private String defaultModel;
    
    @Value("${vault.ai.max-tokens:4096}")
    private Integer maxTokens;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 同步调用AI（非流式）
     */
    public AIResponse chat(List<AIMessage> messages, AIRequest request) {
        try {
            // 构建请求体
            String requestBody = buildRequestBody(messages, request, false);
            
            // 发送HTTP请求
            String responseStr = sendRequest(requestBody);
            
            // 解析响应
            return parseResponse(responseStr);
            
        } catch (Exception e) {
            log.error("AI chat request failed", e);
            throw new RuntimeException("AI service error", e);
        }
    }
    
    /**
     * 流式调用AI
     */
    public void chatStream(List<AIMessage> messages, AIRequest request, StreamCallback callback) {
        new Thread(() -> {
            try {
                // 构建请求体
                String requestBody = buildRequestBody(messages, request, true);
                
                // 发送流式请求
                sendStreamRequest(requestBody, callback);
                
            } catch (Exception e) {
                log.error("AI stream request failed", e);
                callback.onError(e);
            }
        }).start();
    }
    
    /**
     * 构建请求体 - OpenAI/DeepSeek API格式
     */
    private String buildRequestBody(List<AIMessage> messages, AIRequest request, boolean stream) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"model\":\"").append(request != null && request.getModel() != null ? request.getModel() : defaultModel).append("\",");
            
            // OpenAI/DeepSeek API: 所有消息（包括system）都在messages数组中
            json.append("\"messages\":[");
            for (int i = 0; i < messages.size(); i++) {
                AIMessage msg = messages.get(i);
                json.append("{");
                json.append("\"role\":\"").append(msg.getRole()).append("\",");
                json.append("\"content\":").append(objectMapper.writeValueAsString(msg.getContent()));
                json.append("}");
                if (i < messages.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            
            // max_tokens（可选）
            if (request != null && request.getMaxTokens() != null) {
                json.append(",\"max_tokens\":").append(request.getMaxTokens());
            } else if (maxTokens != null) {
                json.append(",\"max_tokens\":").append(maxTokens);
            }
            
            // temperature（可选）
            if (request != null && request.getTemperature() != null) {
                json.append(",\"temperature\":").append(request.getTemperature());
            }
            
            // stream参数
            json.append(",\"stream\":").append(stream);
            json.append("}");
            
            return json.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to build request body", e);
        }
    }
    
    /**
     * 发送HTTP请求 - 使用Claude API认证方式
     */
    private String sendRequest(String requestBody) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        // DeepSeek API使用Authorization Bearer
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        
        // 发送请求
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }
        
        // 读取响应
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    error.append(line);
                }
                log.error("API request failed with code {}: {}", responseCode, error);
            }
            throw new RuntimeException("API request failed with code: " + responseCode);
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    
    /**
     * 发送流式请求 - Claude API格式
     */
    private void sendStreamRequest(String requestBody, StreamCallback callback) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        
        // 发送请求
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }
        
        // 读取流式响应
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            StringBuilder fullContent = new StringBuilder();
            
            while ((line = br.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    
                    // OpenAI/DeepSeek API的结束标记
                    if (data.trim().isEmpty() || "[DONE]".equals(data.trim())) {
                        break;
                    }
                    
                    try {
                        JsonNode json = objectMapper.readTree(data);
                        
                        // OpenAI API: choices[0].delta.content包含实际内容
                        JsonNode choices = json.path("choices");
                        if (choices.isArray() && choices.size() > 0) {
                            JsonNode delta = choices.get(0).path("delta");
                            if (delta.has("content")) {
                                String content = delta.get("content").asText();
                                fullContent.append(content);
                                callback.onNext(content);
                            }
                        }
                        
                    } catch (Exception e) {
                        log.warn("Failed to parse stream data: {}", data, e);
                    }
                }
            }
            
            // 构建完整响应
            AIResponse response = new AIResponse();
            response.setContent(fullContent.toString());
            response.setModel(defaultModel);
            callback.onComplete(response);
        }
    }
    
    /**
     * 解析响应 - OpenAI/DeepSeek API格式
     */
    private AIResponse parseResponse(String responseStr) throws Exception {
        JsonNode json = objectMapper.readTree(responseStr);
        
        AIResponse response = new AIResponse();
        
        // OpenAI API响应格式
        // choices[0].message.content
        JsonNode choices = json.path("choices");
        if (choices.isArray() && choices.size() > 0) {
            JsonNode firstChoice = choices.get(0);
            String content = firstChoice.path("message").path("content").asText();
            response.setContent(content);
            response.setFinishReason(firstChoice.path("finish_reason").asText());
        }
        
        response.setModel(json.path("model").asText());
        
        // 解析token使用情况
        JsonNode usage = json.path("usage");
        if (!usage.isMissingNode()) {
            AIResponse.TokenUsage tokenUsage = new AIResponse.TokenUsage();
            tokenUsage.setPromptTokens(usage.path("prompt_tokens").asInt());
            tokenUsage.setCompletionTokens(usage.path("completion_tokens").asInt());
            tokenUsage.setTotalTokens(usage.path("total_tokens").asInt());
            response.setUsage(tokenUsage);
        }
        
        return response;
    }
    
    /**
     * 生成系统提示词
     */
    public String buildSystemPrompt() {
        return "你是Vault智能教学助手，专门帮助学生学习计算机课程。" +
               "你的回答应该：\n" +
               "1. 基于提供的参考资料给出准确的答案\n" +
               "2. 引用资料来源，便于学生深入学习\n" +
               "3. 用通俗易懂的语言解释复杂概念\n" +
               "4. 适当举例说明，帮助理解\n" +
               "5. 鼓励学生独立思考\n";
    }
}
