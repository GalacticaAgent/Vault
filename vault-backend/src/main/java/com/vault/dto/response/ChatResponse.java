package com.vault.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "聊天会话响应")
public class ChatResponse {
    
    @Schema(description = "会话ID")
    private Long id;
    
    @Schema(description = "会话标题")
    private String title;
    
    @Schema(description = "最后一条消息")
    private String lastMessage;
    
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;
    
    @Schema(description = "消息总数")
    private Integer messageCount;
    
    @Schema(description = "是否置顶")
    private Boolean pinned;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "消息列表（详细查询时返回）")
    private List<MessageResponse> messages;
}
