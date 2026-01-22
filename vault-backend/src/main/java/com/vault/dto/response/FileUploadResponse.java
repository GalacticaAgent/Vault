package com.vault.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件上传响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadResponse {
    
    @Schema(description = "文件ID")
    private Long id;
    
    @Schema(description = "文件名称")
    private String fileName;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "编程语言")
    private String language;
    
    @Schema(description = "提取的内容摘要")
    private String contentSummary;
    
    @Schema(description = "上传时间")
    private LocalDateTime createTime;
}
