package com.vault.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 材料响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "材料响应")
public class MaterialResponse {
    
    @Schema(description = "材料ID")
    private Long id;
    
    @Schema(description = "材料名称")
    private String name;
    
    @Schema(description = "材料描述")
    private String description;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "上传者ID")
    private Long uploaderId;
    
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;
    
    @Schema(description = "浏览次数")
    private Integer viewCount;
}
