package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷提交记录实体类
 */
@Data
@TableName("questionnaire_submissions")
public class QuestionnaireSubmission {

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
