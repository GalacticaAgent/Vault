package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天文件实体类
 * 表示用户在对话中上传的文件
 */
@Data
@TableName("chat_files")
public class ChatFile {
    
    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 所属会话ID
     */
    private Long chatId;
    
    /**
     * 所属消息ID
     */
    private Long messageId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 文件原始名称
     */
    private String fileName;
    
    /**
     * 文件类型：code-代码文件，pdf-PDF文档，image-图片，other-其他
     */
    private String fileType;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件存储路径（MinIO）
     */
    private String filePath;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 提取的文本内容
     */
    @TableField(jdbcType = org.apache.ibatis.type.JdbcType.LONGVARCHAR)
    private String extractedContent;
    
    /**
     * 编程语言（如果是代码文件）
     */
    private String language;
    
    /**
     * 处理状态：pending-待处理，processing-处理中，completed-已完成，failed-失败
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
