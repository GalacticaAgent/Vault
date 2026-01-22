package com.vault.service.skill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.model.skill.RoutingDecision;
import com.vault.service.ai.AIMessage;
import com.vault.service.ai.AIRequest;
import com.vault.service.ai.AIResponse;
import com.vault.service.ai.AIService;
import com.vault.util.JsonSchemaValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill路由器 - 完全对标 chat-skills 的 SkillRouterAgent
 * 支持LLM智能路由 + JSON Schema验证
 */
@Slf4j
@Service
public class SkillRouter {
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private SkillLoader skillLoader;
    
    @Autowired
    private JsonSchemaValidator schemaValidator;
    
    @Value("${vault.skill.router.max-skills:3}")
    private int maxSkills;
    
    @Value("${vault.skill.router.max-retries:2}")
    private int maxRetries;
    
    @Value("${vault.skill.router.use-llm:true}")
    private boolean useLLM;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public RoutingDecision route(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return RoutingDecision.noSkills("Empty message");
        }
        
        if (useLLM) {
            return routeWithLLM(userMessage);
        } else {
            return routeWithKeywords(userMessage);
        }
    }
    
    private RoutingDecision routeWithLLM(String userMessage) {
        String systemPrompt = buildSystemPrompt();
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                RoutingDecision decision = getRoutingDecisionFromLLM(userMessage, systemPrompt, attempt);
                
                if (validateDecision(decision)) {
                    log.info("Routing decision (attempt {}): use_skills={}, skills={}", 
                        attempt + 1, decision.getUseSkills(), decision.getSelectedSkills());
                    return decision;
                }
            } catch (Exception e) {
                log.warn("Routing attempt {} failed: {}", attempt + 1, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("All routing attempts failed, falling back to no skills", e);
                }
            }
        }
        
        return RoutingDecision.noSkills("Routing failed, proceeding without skills");
    }
    
    private String buildSystemPrompt() {
        String skillIndex = skillLoader.generateIndex();
        List<String> availableSkills = skillLoader.getSkillNames();
        
        return String.format("""
            You are a skill routing assistant. Your job is to analyze user queries and decide whether to use specialized skills to handle them.
            
            %s
            
            INSTRUCTIONS:
            1. Analyze the user's message carefully
            2. Decide if any skills would be helpful (0 to %d skills)
            3. Only select skills that are DIRECTLY relevant to the user's request
            4. Respond with ONLY a JSON object, no other text
            
            OUTPUT FORMAT (strict JSON only):
            {
              "use_skills": true or false,
              "selected_skills": ["skill_name_1", "skill_name_2"],
              "rationale": "One short sentence"
            }
            
            RULES:
            - selected_skills must contain 0 to %d skill names
            - Only use skill names from the available skills list
            - For casual chat or simple queries, use_skills should be false
            - The rationale must be one concise sentence (max 200 chars)
            
            VALID SKILL NAMES:
            %s
            
            OUTPUT ONLY THE JSON OBJECT, NO OTHER TEXT.
            """,
            skillIndex,
            maxSkills,
            maxSkills,
            availableSkills.isEmpty() ? "None" : String.join(", ", availableSkills)
        );
    }
    
    private RoutingDecision getRoutingDecisionFromLLM(String userMessage, String systemPrompt, int attempt) 
            throws Exception {
        
        List<AIMessage> messages = new ArrayList<>();
        messages.add(new AIMessage("system", systemPrompt));
        messages.add(new AIMessage("user", userMessage));
        
        AIResponse response = aiService.chat(messages, null);
        String responseText = response.getContent();
        
        String cleanedResponse = cleanJsonResponse(responseText);
        
        JsonSchemaValidator.ValidationResult validation = 
            schemaValidator.validateRoutingDecision(cleanedResponse);
        
        if (!validation.isValid()) {
            throw new IllegalArgumentException(
                "JSON Schema validation failed: " + validation.getErrorMessage());
        }
        
        JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
        RoutingDecision decision = objectMapper.treeToValue(jsonNode, RoutingDecision.class);
        
        validateSkillNames(decision);
        decision.truncateRationale(200);
        
        return decision;
    }
    
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        
        if (cleaned.startsWith("```")) {
            String[] lines = cleaned.split("\n");
            if (lines.length > 2) {
                List<String> middleLines = new ArrayList<>();
                for (int i = 1; i < lines.length - 1; i++) {
                    middleLines.add(lines[i]);
                }
                cleaned = String.join("\n", middleLines).trim();
            }
            
            if (cleaned.toLowerCase().startsWith("json")) {
                cleaned = cleaned.substring(4).trim();
            }
        }
        
        return cleaned;
    }
    
    private void validateSkillNames(RoutingDecision decision) throws IllegalArgumentException {
        if (!decision.getUseSkills() || decision.getSelectedSkills().isEmpty()) {
            return;
        }
        
        List<String> availableSkills = skillLoader.getSkillNames();
        List<String> invalidSkills = new ArrayList<>();
        
        for (String skillName : decision.getSelectedSkills()) {
            if (!availableSkills.contains(skillName.toLowerCase())) {
                invalidSkills.add(skillName);
            }
        }
        
        if (!invalidSkills.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("Invalid skill names: %s. Valid skills: %s",
                    invalidSkills, availableSkills));
        }
        
        if (decision.getSelectedSkills().size() > maxSkills) {
            throw new IllegalArgumentException(
                String.format("Too many skills selected: %d (max: %d)",
                    decision.getSelectedSkills().size(), maxSkills));
        }
    }
    
    private boolean validateDecision(RoutingDecision decision) {
        if (decision == null) {
            log.warn("Decision is null");
            return false;
        }
        
        if (!decision.isValid()) {
            log.warn("Decision validation failed");
            return false;
        }
        
        return true;
    }
    
    private RoutingDecision routeWithKeywords(String userMessage) {
        String messageLower = userMessage.toLowerCase();
        List<String> matchedSkills = new ArrayList<>();
        
        for (SkillDefinition skill : skillLoader.getAllSkills()) {
            for (String keyword : skill.getTriggerKeywords()) {
                if (messageLower.contains(keyword.toLowerCase())) {
                    matchedSkills.add(skill.getName());
                    break;
                }
            }
            
            if (matchedSkills.size() >= maxSkills) {
                break;
            }
        }
        
        if (matchedSkills.isEmpty()) {
            return RoutingDecision.noSkills("No matching skills found");
        } else {
            return RoutingDecision.withSkills(matchedSkills, 
                "Matched " + matchedSkills.size() + " skill(s) by keywords");
        }
    }
    
    public SkillDefinition routeToSkill(String userInput) {
        RoutingDecision decision = route(userInput);
        
        if (!decision.getUseSkills() || decision.getSelectedSkills().isEmpty()) {
            return null;
        }
        
        String firstSkillName = decision.getSelectedSkills().get(0);
        return skillLoader.getSkillByName(firstSkillName);
    }
    
    public List<SkillDefinition> routeToSkills(String userInput) {
        RoutingDecision decision = route(userInput);
        
        if (!decision.getUseSkills() || decision.getSelectedSkills().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<SkillDefinition> skills = new ArrayList<>();
        for (String skillName : decision.getSelectedSkills()) {
            SkillDefinition skill = skillLoader.getSkillByName(skillName);
            if (skill != null) {
                skills.add(skill);
            }
        }
        
        return skills;
    }
}
