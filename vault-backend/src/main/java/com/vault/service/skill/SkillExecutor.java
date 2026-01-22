package com.vault.service.skill;

import com.vault.service.ai.AIService;
import com.vault.service.chat.RAGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Skill执行器
 * 参考chat-skills中的执行逻辑
 * 负责执行Skill的工作流
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillExecutor {
    
    private final AIService aiService;
    private final RAGService ragService;
    
    // KnowledgeGraphService 暂时注释，等待实现
    // private final KnowledgeGraphService knowledgeGraphService;
    
    /**
     * 执行Skill
     */
    public SkillResult execute(SkillDefinition skill, SkillContext context) {
        log.info("Executing skill: {} for user: {}", skill.getName(), context.getUserId());
        
        SkillResult result = new SkillResult();
        result.setSkillId(skill.getId());
        result.setSkillName(skill.getName());
        result.setSuccess(true);
        
        try {
            // 初始化变量
            if (context.getVariables() == null) {
                context.setVariables(new HashMap<>());
            }
            context.getVariables().put("user_input", context.getUserInput());
            
            // 执行工作流步骤
            if (skill.getSteps() != null) {
                for (SkillDefinition.WorkflowStep step : skill.getSteps()) {
                    executeStep(step, context);
                }
            }
            
            // 格式化输出
            String output = formatOutput(skill, context);
            result.setOutput(output);
            result.setVariables(context.getVariables());
            
        } catch (Exception e) {
            log.error("Failed to execute skill: {}", skill.getId(), e);
            result.setSuccess(false);
            result.setError(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 执行单个步骤
     */
    private void executeStep(SkillDefinition.WorkflowStep step, SkillContext context) {
        log.debug("Executing step: {}", step.getId());
        
        String stepType = step.getType();
        if (stepType == null) {
            // 从描述推断类型
            stepType = inferStepType(step.getDescription());
        }
        
        Object result = null;
        
        switch (stepType.toLowerCase()) {
            case "rag_search":
            case "search":
                result = executeRAGSearch(step, context);
                break;
                
            case "llm_call":
            case "ai_call":
                result = executeLLMCall(step, context);
                break;
                
            case "knowledge_query":
                result = executeKnowledgeQuery(step, context);
                break;
                
            case "data_process":
            case "transform":
                result = executeDataProcess(step, context);
                break;
                
            default:
                log.warn("Unknown step type: {}", stepType);
        }
        
        // 保存结果到变量
        if (step.getOutputVariable() != null && result != null) {
            context.getVariables().put(step.getOutputVariable(), result);
        }
    }
    
    /**
     * 从描述推断步骤类型
     */
    private String inferStepType(String description) {
        String lowerDesc = description.toLowerCase();
        if (lowerDesc.contains("search") || lowerDesc.contains("检索") || lowerDesc.contains("查找")) {
            return "rag_search";
        } else if (lowerDesc.contains("call") || lowerDesc.contains("ai") || lowerDesc.contains("生成")) {
            return "llm_call";
        } else if (lowerDesc.contains("knowledge") || lowerDesc.contains("知识点")) {
            return "knowledge_query";
        }
        return "data_process";
    }
    
    /**
     * 执行RAG检索
     */
    private Object executeRAGSearch(SkillDefinition.WorkflowStep step, SkillContext context) {
        String query = (String) context.getVariables().get("user_input");
        
        // 从参数中获取配置
        Map<String, Object> params = step.getParameters();
        int topK = params != null && params.containsKey("top_k") ? 
                   (Integer) params.get("top_k") : 5;
        
        try {
            return ragService.search(query, topK);
        } catch (Exception e) {
            log.error("RAG search failed", e);
            return null;
        }
    }
    
    /**
     * 执行LLM调用
     */
    private Object executeLLMCall(SkillDefinition.WorkflowStep step, SkillContext context) {
        Map<String, Object> params = step.getParameters();
        if (params == null) {
            params = new HashMap<>();
        }
        
        // 构建prompt
        String prompt = buildPrompt(step, context);
        
        // 调用AI
        try {
            String model = (String) params.getOrDefault("model", "gpt-4");
            Double temperature = params.containsKey("temperature") ? 
                               ((Number) params.get("temperature")).doubleValue() : 0.7;
            
            // 这里应该调用AIService的方法
            // 简化处理，返回模拟结果
            return "AI response for: " + prompt;
            
        } catch (Exception e) {
            log.error("LLM call failed", e);
            return null;
        }
    }
    
    /**
     * 执行知识点查询
     */
    private Object executeKnowledgeQuery(SkillDefinition.WorkflowStep step, SkillContext context) {
        // 从Neo4j查询知识点相关信息
        // 这里需要注入KnowledgeGraphService
        return null;
    }
    
    /**
     * 执行数据处理
     */
    private Object executeDataProcess(SkillDefinition.WorkflowStep step, SkillContext context) {
        // 简单的数据转换和处理
        return step.getDescription();
    }
    
    /**
     * 构建Prompt
     */
    private String buildPrompt(SkillDefinition.WorkflowStep step, SkillContext context) {
        Map<String, Object> params = step.getParameters();
        if (params != null && params.containsKey("prompt_template")) {
            String template = (String) params.get("prompt_template");
            return replaceVariables(template, context.getVariables());
        }
        return step.getDescription();
    }
    
    /**
     * 替换模板变量
     */
    private String replaceVariables(String template, Map<String, Object> variables) {
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }
        return result;
    }
    
    /**
     * 格式化输出
     */
    private String formatOutput(SkillDefinition skill, SkillContext context) {
        if (skill.getOutput() != null && skill.getOutput().getTemplate() != null) {
            return replaceVariables(skill.getOutput().getTemplate(), context.getVariables());
        }
        
        // 默认返回最后一个变量的值
        if (!context.getVariables().isEmpty()) {
            Object lastValue = context.getVariables().values().toArray()[context.getVariables().size() - 1];
            return String.valueOf(lastValue);
        }
        
        return "Skill executed successfully";
    }
}
