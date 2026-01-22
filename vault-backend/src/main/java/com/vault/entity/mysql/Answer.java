package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
<<<<<<< HEAD
=======

>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答案实体类
 */
@Data
@TableName("answers")
public class Answer {

<<<<<<< HEAD
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionnaireId;

    private Long questionId;

    private Long studentId;

    private String answerContent;

    private Boolean isCorrect;

    private BigDecimal score;

    private String feedback;

    private Integer timeSpent;

=======
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
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
