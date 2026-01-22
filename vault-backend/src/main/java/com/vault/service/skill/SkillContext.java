package com.vault.service.skill;

import lombok.Data;
import java.util.Map;

/**
 * Skill执行上下文
 */
@Data
public class SkillContext {
    
    /**
     * 用户输入
     */
    private String userInput;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID
     */
    private Long chatId;
    
    /**
     * 输入参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 中间变量（用于步骤间传递数据）
     */
    private Map<String, Object> variables;
    
    /**
     * RAG检索结果
     */
    private Object ragResults;
    
    /**
     * 历史消息
     */
    private Object messageHistory;
    
    /**
     * 学生肖像数据
     */
    private Object studentProfile;
    
    /**
     * 知识点信息
     */
    private Object knowledgePoints;
}
