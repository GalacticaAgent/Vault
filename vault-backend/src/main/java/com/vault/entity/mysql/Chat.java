package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 * 表示一个完整的对话会话
 */
@Data
@TableName("chats")
public class Chat {
    
    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话标题（自动从第一条消息生成或用户自定义）
     */
    private String title;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话状态：active-活跃，archived-归档，deleted-已删除
     */
    @TableField("status")
    private String status;
    
    /**
     * 最后一条消息内容（用于预览）
     */
    private String lastMessage;
    
    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 消息总数
     */
    private Integer messageCount;
    
    /**
     * 是否置顶
     */
    private Boolean pinned;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
