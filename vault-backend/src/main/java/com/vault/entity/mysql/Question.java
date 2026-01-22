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
 * 题目实体类
 */
@Data
<<<<<<< HEAD
@TableName("questions")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionnaireId;

    private Integer questionOrder;

    private String type;

    private String content;

    private String options;

    private String correctAnswer;

    private String knowledgePoint;

    private String difficulty;

    private BigDecimal score;

    private String explanation;

=======
@TableName(value = "questions", autoResultMap = true)
public class Question {

    /**
     * 题目ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 题目顺序
     */
    private Integer questionOrder;

    /**
     * 题目类型：SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, CODING
     */
    private String type;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 选项（选择题使用，JSON格式）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 关联知识点
     */
    private String knowledgePoint;

    /**
     * 难度：EASY, MEDIUM, HARD
     */
    private String difficulty;

    /**
     * 分值
     */
    private BigDecimal score;

    /**
     * 题目解析
     */
    private String explanation;

    /**
     * 创建时间
     */
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
