package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 上传材料请求
 */
@Data
@Schema(description = "上传材料请求")
public class UploadMaterialRequest {
    
    @Schema(description = "材料名称")
    @NotBlank(message = "材料名称不能为空")
    private String name;
    
    @Schema(description = "材料描述")
    private String description;
    
    @Schema(description = "课程ID")
    private Long courseId;
    
    @Schema(description = "材料类型")
    private String type;
}
