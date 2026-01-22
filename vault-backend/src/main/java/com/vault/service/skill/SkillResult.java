package com.vault.service.skill;

import lombok.Data;
import java.util.Map;

/**
 * Skill执行结果
 */
@Data
public class SkillResult {
    
    /**
     * Skill ID
     */
    private String skillId;
    
    /**
     * Skill名称
     */
    private String skillName;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 输出结果
     */
    private String output;
    
    /**
     * 执行过程中的变量
     */
    private Map<String, Object> variables;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 执行耗时（毫秒）
     */
    private long executionTime;
}
