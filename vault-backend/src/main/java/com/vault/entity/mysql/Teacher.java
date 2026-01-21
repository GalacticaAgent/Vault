package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@Data
@TableName("teachers")
public class Teacher {
    
    /**
     * 教师ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 工号
     */
    private String teacherNumber;
    
    /**
     * 所属院系
     */
    private String department;
    
    /**
     * 职称
     */
    private String title;
    
    /**
     * 授课课程列表（JSON）
     */
    private String courses;
    
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
}

