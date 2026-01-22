package com.vault.service.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.config.DeepSeekConfig;
import com.vault.exception.BusinessException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

/**
 * DeepSeek AI 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {

    private final DeepSeekConfig deepSeekConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用 DeepSeek API 生成文本
     *
     * @param prompt 提示词
     * @return AI 生成的文本
     */
    public String chat(String prompt) {
        try {
            // 构建请求体
            ChatRequest request = new ChatRequest();
            request.setModel(deepSeekConfig.getModel());
            request.setMessages(List.of(new Message("user", prompt)));
            request.setMaxTokens(deepSeekConfig.getMaxTokens());
            request.setTemperature(deepSeekConfig.getTemperature());

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepSeekConfig.getApiKey());

            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

            log.info("调用 DeepSeek API: model={}, prompt_length={}",
                     deepSeekConfig.getModel(), prompt.length());

            // 发送请求
            ResponseEntity<ChatResponse> response = restTemplate.exchange(
                    deepSeekConfig.getApiUrl(),
                    HttpMethod.POST,
                    entity,
                    ChatResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ChatResponse chatResponse = response.getBody();
                if (chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
                    String content = chatResponse.getChoices().get(0).getMessage().getContent();
                    log.info("DeepSeek API 调用成功: response_length={}", content.length());
                    return content;
                }
            }

            log.error("DeepSeek API 返回空响应");
            throw new BusinessException(500, "AI 服务返回空响应");

        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI 服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 聊天请求
     */
    @Data
    private static class ChatRequest {
        private String model;
        private List<Message> messages;

        @JsonProperty("max_tokens")
        private Integer maxTokens;

        private Double temperature;
    }

    /**
     * 消息
     */
    @Data
    private static class Message {
        private String role;
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * 聊天响应
     */
    @Data
    private static class ChatResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;
    }

    /**
     * 选择项
     */
    @Data
    private static class Choice {
        private Integer index;
        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;
    }

    /**
     * Token 使用情况
     */
    @Data
    private static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}
