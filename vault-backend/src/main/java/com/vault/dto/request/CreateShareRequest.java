package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 创建分享请求
 */
@Data
@Schema(description = "创建分享请求")
public class CreateShareRequest {
    
    @NotNull(message = "会话ID不能为空")
    @Schema(description = "会话ID", required = true)
    private Long chatId;
    
    @Schema(description = "分享标题")
    private String title;
    
    @Schema(description = "分享描述")
    private String description;
    
    @Schema(description = "过期天数（null表示永不过期）", example = "7")
    private Integer expireDays;
    
    @Schema(description = "是否需要密码", example = "false")
    private Boolean requirePassword = false;
    
    @Schema(description = "访问密码（如果需要）")
    private String password;
}
