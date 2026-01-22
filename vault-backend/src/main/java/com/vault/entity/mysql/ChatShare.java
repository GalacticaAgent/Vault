package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 对话分享实体类
 * 表示一个可分享的对话链接
 */
@Data
@TableName("chat_shares")
public class ChatShare {
    
    /**
     * 分享ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分享的唯一标识符（UUID）
     */
    private String shareId;
    
    /**
     * 会话ID
     */
    private Long chatId;
    
    /**
     * 分享者用户ID
     */
    private Long userId;
    
    /**
     * 分享标题
     */
    private String title;
    
    /**
     * 分享描述
     */
    private String description;
    
    /**
     * 访问次数
     */
    private Integer viewCount;
    
    /**
     * 过期时间（null表示永不过期）
     */
    private LocalDateTime expireTime;
    
    /**
     * 是否需要密码
     */
    private Boolean requirePassword;
    
    /**
     * 访问密码（加密存储）
     */
    private String password;
    
    /**
     * 状态：active-活跃，expired-已过期，revoked-已撤销
     */
    private String status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
