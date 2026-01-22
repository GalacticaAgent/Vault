package com.vault.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息响应")
public class MessageResponse {
    
    @Schema(description = "消息ID")
    private Long id;
    
    @Schema(description = "消息角色：user/assistant/system")
    private String role;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "关联的文件列表")
    private List<FileInfo> files;
    
    @Schema(description = "引用的资料信息")
    private List<MaterialReference> references;
    
    @Schema(description = "使用的Skill")
    private String skillUsed;
    
    @Schema(description = "涉及的知识点")
    private List<String> knowledgePoints;
    
    @Schema(description = "Token消耗")
    private TokenUsage tokenUsage;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    /**
     * 文件信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfo {
        private Long id;
        private String fileName;
        private String fileType;
        private String fileUrl;
        private String language;
    }
    
    /**
     * 资料引用信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialReference {
        private Long materialId;
        private String materialName;
        private String chapter;
        private String content;
        private Double relevance;
    }
    
    /**
     * Token使用情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenUsage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
