package com.vault.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分享响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分享响应")
public class ShareResponse {
    
    @Schema(description = "分享ID")
    private String shareId;
    
    @Schema(description = "分享URL")
    private String shareUrl;
    
    @Schema(description = "分享标题")
    private String title;
    
    @Schema(description = "分享描述")
    private String description;
    
    @Schema(description = "访问次数")
    private Integer viewCount;
    
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
    
    @Schema(description = "是否需要密码")
    private Boolean requirePassword;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
