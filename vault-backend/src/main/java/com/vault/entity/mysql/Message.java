package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类
 * 表示对话中的单条消息
 */
@Data
@TableName("messages")
public class Message {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 所属会话ID
     */
    private Long chatId;
    
    /**
     * 消息角色：user-用户，assistant-AI助手，system-系统
     */
    private String role;
    
    /**
     * 消息内容
     */
    @TableField(value = "content", jdbcType = org.apache.ibatis.type.JdbcType.LONGVARCHAR)
    private String content;
    
    /**
     * 相关文件ID列表（JSON格式）
     */
    private String fileIds;
    
    /**
     * 引用的资料ID列表（JSON格式）
     */
    private String materialIds;
    
    /**
     * 引用的资料片段信息（JSON格式）
     */
    @TableField(value = "`references`", jdbcType = org.apache.ibatis.type.JdbcType.LONGVARCHAR)
    private String references;
    
    /**
     * 使用的Skill名称
     */
    private String skillUsed;
    
    /**
     * Token消耗统计（JSON格式）
     */
    private String tokenUsage;
    
    /**
     * 涉及的知识点列表（JSON格式）
     */
    private String knowledgePoints;
    
    /**
     * 消息状态：sending-发送中，sent-已发送，failed-失败
     */
    private String status;
    
    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
    
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
