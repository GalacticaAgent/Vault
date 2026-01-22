package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答案实体类
 */
@Data
@TableName("answers")
public class Answer {

    /**
     * 答案ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 答案内容
     */
    private String answerContent;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * AI反馈
     */
    private String feedback;

    /**
     * 答题用时（秒）
     */
    private Integer timeSpent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
