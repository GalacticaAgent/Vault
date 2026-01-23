package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 更新用户信息请求
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateUserRequest {
    
    @Schema(description = "昵称", example = "张三")
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    
    @Schema(description = "头像URL", example = "/uploads/2026/01/23/avatar.jpg")
    private String avatar;
}
