package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
<<<<<<< HEAD
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
=======
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed

/**
 * 问卷实体类
 */
@Data
<<<<<<< HEAD
@TableName("questionnaires")
public class Questionnaire {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private Long creatorId;

    private String targetStudents;

    private Integer timeLimit;

    private BigDecimal totalScore;

    private BigDecimal passScore;

    private LocalDateTime startTime;

    private LocalDateTime deadline;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

=======
@TableName(value = "questionnaires", autoResultMap = true)
public class Questionnaire {

    /**
     * 问卷ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 问卷标题
     */
    private String title;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 创建者ID（教师）
     */
    private Long creatorId;

    /**
     * 目标学生ID列表（JSON，空表示全班）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> targetStudents;

    /**
     * 时间限制（分钟）
     */
    private Integer timeLimit;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 及格分数
     */
    private BigDecimal passScore;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 状态：DRAFT, PUBLISHED, CLOSED
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
