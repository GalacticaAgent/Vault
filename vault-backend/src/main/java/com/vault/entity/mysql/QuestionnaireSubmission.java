package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
<<<<<<< HEAD
=======

>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷提交记录实体类
 */
@Data
@TableName("questionnaire_submissions")
public class QuestionnaireSubmission {

<<<<<<< HEAD
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionnaireId;

    private Long studentId;

    private BigDecimal totalScore;

    private Integer timeSpent;

    private LocalDateTime submitTime;

=======
    /**
     * 提交ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 总得分
     */
    private BigDecimal totalScore;

    /**
     * 总用时（秒）
     */
    private Integer timeSpent;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 创建时间
     */
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
