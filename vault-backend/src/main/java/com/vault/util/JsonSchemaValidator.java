package com.vault.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * JSON Schema验证工具类
 * 对标 chat-skills 的 Pydantic 验证机制
 */
@Slf4j
@Component
public class JsonSchemaValidator {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    
    /**
     * 路由决策的JSON Schema定义
     * 对应 chat-skills 中的 RoutingDecision Pydantic model
     */
    private static final String ROUTING_DECISION_SCHEMA = """
            {
              "$schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "required": ["use_skills", "selected_skills", "rationale"],
              "properties": {
                "use_skills": {
                  "type": "boolean",
                  "description": "Whether to use any skills for this query"
                },
                "selected_skills": {
                  "type": "array",
                  "items": {
                    "type": "string"
                  },
                  "description": "List of skill names to use (0 to N skills)",
                  "default": []
                },
                "rationale": {
                  "type": "string",
                  "description": "One short sentence explaining the decision",
                  "minLength": 1,
                  "maxLength": 200
                }
              },
              "additionalProperties": false
            }
            """;
    
    private JsonSchema routingDecisionSchema;
    
    public JsonSchemaValidator() {
        try {
            JsonNode schemaNode = objectMapper.readTree(ROUTING_DECISION_SCHEMA);
            this.routingDecisionSchema = factory.getSchema(schemaNode);
        } catch (Exception e) {
            log.error("Failed to initialize routing decision schema", e);
        }
    }
    
    /**
     * 验证路由决策JSON是否符合Schema
     * 
     * @param jsonString 待验证的JSON字符串
     * @return 验证结果
     */
    public ValidationResult validateRoutingDecision(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            Set<ValidationMessage> errors = routingDecisionSchema.validate(jsonNode);
            
            if (errors.isEmpty()) {
                return ValidationResult.success();
            } else {
                StringBuilder errorMsg = new StringBuilder("JSON Schema validation failed: ");
                errors.forEach(error -> errorMsg.append(error.getMessage()).append("; "));
                return ValidationResult.failure(errorMsg.toString());
            }
        } catch (Exception e) {
            return ValidationResult.failure("Failed to parse JSON: " + e.getMessage());
        }
    }
    
    /**
     * 验证自定义Schema
     * 
     * @param jsonString 待验证的JSON字符串
     * @param schemaString JSON Schema定义
     * @return 验证结果
     */
    public ValidationResult validate(String jsonString, String schemaString) {
        try {
            JsonNode schemaNode = objectMapper.readTree(schemaString);
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            
            JsonSchema schema = factory.getSchema(schemaNode);
            Set<ValidationMessage> errors = schema.validate(jsonNode);
            
            if (errors.isEmpty()) {
                return ValidationResult.success();
            } else {
                StringBuilder errorMsg = new StringBuilder("JSON Schema validation failed: ");
                errors.forEach(error -> errorMsg.append(error.getMessage()).append("; "));
                return ValidationResult.failure(errorMsg.toString());
            }
        } catch (Exception e) {
            return ValidationResult.failure("Failed to validate: " + e.getMessage());
        }
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
