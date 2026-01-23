package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 执行技能请求
 */
@Data
@Schema(description = "执行技能请求")
public class ExecuteSkillRequest {
    
    @Schema(description = "技能参数")
    @NotNull(message = "技能参数不能为空")
    private Map<String, Object> params;
    
    @Schema(description = "是否流式输出")
    private Boolean stream = false;
}
