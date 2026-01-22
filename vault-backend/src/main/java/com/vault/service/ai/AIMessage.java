package com.vault.service.ai;

import lombok.Data;
import java.util.List;

/**
 * AI消息
 */
@Data
public class AIMessage {
    
    /**
     * 角色：system, user, assistant
     */
    private String role;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 函数调用（如果有）
     */
    private FunctionCall functionCall;
    
    public AIMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    @Data
    public static class FunctionCall {
        private String name;
        private String arguments;
    }
    
    public static AIMessage system(String content) {
        return new AIMessage("system", content);
    }
    
    public static AIMessage user(String content) {
        return new AIMessage("user", content);
    }
    
    public static AIMessage assistant(String content) {
        return new AIMessage("assistant", content);
    }
}
