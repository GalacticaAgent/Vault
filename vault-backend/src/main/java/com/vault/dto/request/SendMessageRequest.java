package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 发送消息请求
 */
@Data
@Schema(description = "发送消息请求")
public class SendMessageRequest {
    
    @Schema(description = "会话ID（首次对话可为空）")
    private Long chatId;
    
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "消息内容", required = true)
    private String content;
    
    @Schema(description = "上传的文件ID列表")
    private List<Long> fileIds;
    
    @Schema(description = "是否流式返回", example = "true")
    private Boolean stream = true;
    
    @Schema(description = "使用的AI模型", example = "gpt-4")
    private String model;
    
    @Schema(description = "温度参数（0-2）", example = "0.7")
    private Double temperature = 0.7;
    
    @Schema(description = "最大token数", example = "2000")
    private Integer maxTokens = 2000;
}
