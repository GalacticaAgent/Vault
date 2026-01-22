package com.vault.service.skill;

import lombok.Data;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Skill定义 - 完全对标 chat-skills 的 Skill 类
 * 参考chat-skills中的SKILL.md格式，支持完整的Markdown解析
 */
@Data
public class SkillDefinition {
    
    /**
     * Skill唯一标识（从目录名生成）
     */
    private String id;
    
    /**
     * Skill名称（从Markdown标题提取）
     */
    private String name;
    
    /**
     * Skill描述（从内容前几行提取）
     */
    private String description;
    
    /**
     * 触发条件/适用场景（用于路由判断）
     */
    private String triggers;
    
    /**
     * 触发关键词列表（用于快速匹配）
     */
    private List<String> triggerKeywords = new ArrayList<>();
    
    /**
     * 输入参数定义
     */
    private Map<String, ParameterDefinition> inputs = new HashMap<>();
    
    /**
     * 工作流步骤
     */
    private List<WorkflowStep> steps = new ArrayList<>();
    
    /**
     * 输出格式定义
     */
    private OutputDefinition output;
    
    /**
     * 示例列表
     */
    private List<Example> examples = new ArrayList<>();
    
    /**
     * Skill版本
     */
    private String version;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * Skill完整路径
     */
    private Path fullPath;
    
    /**
     * 原始Markdown内容
     */
    private String content;
    
    /**
     * 加载时间
     */
    private LocalDateTime loadTime;
    
    /**
     * 文件最后修改时间
     */
    private LocalDateTime fileModifiedTime;
    
    /**
     * 是否有效
     */
    private boolean valid = true;
    
    /**
     * 验证错误消息
     */
    private List<String> validationErrors = new ArrayList<>();
    
    /**
     * 生成简洁的索引条目（用于Router的system prompt）
     * 对标 chat-skills 的 to_index_entry 方法
     */
    public String toIndexEntry() {
        StringBuilder entry = new StringBuilder();
        entry.append("- **").append(name).append("**\n");
        
        if (description != null && !description.isEmpty()) {
            entry.append("  ").append(description).append("\n");
        }
        
        if (triggers != null && !triggers.isEmpty()) {
            entry.append("  Use when: ").append(triggers).append("\n");
        }
        
        return entry.toString();
    }
    
    /**
     * 验证Skill定义的完整性
     * 对标 chat-skills 的验证逻辑
     */
    public boolean validate() {
        validationErrors.clear();
        valid = true;
        
        if (name == null || name.trim().isEmpty()) {
            validationErrors.add("Skill name is required");
            valid = false;
        }
        
        if (description == null || description.trim().isEmpty()) {
            validationErrors.add("Skill description is required");
            valid = false;
        }
        
        if (triggers == null || triggers.trim().isEmpty()) {
            validationErrors.add("Skill triggers/when-to-use is required");
            valid = false;
        }
        
        if (steps == null || steps.isEmpty()) {
            validationErrors.add("Skill must have at least one workflow step");
            valid = false;
        }
        
        // 验证工作流步骤
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                WorkflowStep step = steps.get(i);
                if (step.getType() == null || step.getType().trim().isEmpty()) {
                    validationErrors.add("Step " + i + " missing type");
                    valid = false;
                }
            }
        }
        
        return valid;
    }
    
    /**
     * 参数定义
     */
    @Data
    public static class ParameterDefinition {
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;
        private List<String> allowedValues;
    }
    
    /**
     * 工作流步骤 - 增强版本
     */
    @Data
    public static class WorkflowStep {
        private String id;
        private String type; // llm_call, rag_search, code_execute, knowledge_update, etc.
        private String description;
        private Map<String, Object> parameters = new HashMap<>();
        private String outputVariable;
        private String condition; // 条件执行
        private int retryCount = 0; // 重试次数
        private int timeout = 30; // 超时时间（秒）
    }
    
    /**
     * 输出定义
     */
    @Data
    public static class OutputDefinition {
        private String format; // text, markdown, json, html
        private String template;
        private Map<String, String> variables;
    }
    
    /**
     * 示例
     */
    @Data
    public static class Example {
        private String input;
        private String expectedOutput;
        private String description;
    }
}
