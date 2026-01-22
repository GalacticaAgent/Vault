package com.vault.exception;

import lombok.Getter;

/**
 * Skill执行异常 - 支持重试判断
 */
@Getter
public class SkillExecutionException extends RuntimeException {
    
    private final String skillId;
    private final String stepId;
    private final boolean retryable;
    
    public SkillExecutionException(String message, String skillId, String stepId) {
        super(message);
        this.skillId = skillId;
        this.stepId = stepId;
        this.retryable = true;
    }
    
    public SkillExecutionException(String message, String skillId, String stepId, boolean retryable) {
        super(message);
        this.skillId = skillId;
        this.stepId = stepId;
        this.retryable = retryable;
    }
    
    public SkillExecutionException(String message, Throwable cause, String skillId, String stepId) {
        super(message, cause);
        this.skillId = skillId;
        this.stepId = stepId;
        this.retryable = true;
    }
    
    public SkillExecutionException(String message, Throwable cause, String skillId, String stepId, boolean retryable) {
        super(message, cause);
        this.skillId = skillId;
        this.stepId = stepId;
        this.retryable = retryable;
    }
}
