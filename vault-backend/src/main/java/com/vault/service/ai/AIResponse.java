package com.vault.service.ai;

import lombok.Data;

/**
 * AI响应
 */
@Data
public class AIResponse {
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * Token使用情况
     */
    private TokenUsage usage;
    
    /**
     * 完成原因：stop, length, content_filter
     */
    private String finishReason;
    
    @Data
    public static class TokenUsage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
}
