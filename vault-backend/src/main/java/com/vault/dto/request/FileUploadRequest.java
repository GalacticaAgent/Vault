package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

/**
 * 文件上传请求
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadRequest {
    
    @NotNull(message = "文件不能为空")
    @Schema(description = "上传的文件", required = true)
    private MultipartFile file;
    
    @Schema(description = "会话ID（如果在对话中上传）")
    private Long chatId;
    
    @Schema(description = "文件类型", example = "code")
    private String fileType;
    
    @Schema(description = "编程语言（如果是代码）", example = "java")
    private String language;
}
