package com.vault.dto.response;

<<<<<<< HEAD
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷列表响应DTO
 */
@Data
public class QuestionnaireListResponse {

    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private BigDecimal totalScore;
    private LocalDateTime deadline;
    private String status;
    private Boolean isCompleted;
    private BigDecimal myScore;
    private LocalDateTime submitTime;
=======
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷列表响应 DTO
 */
@Data
@Builder
public class QuestionnaireListResponse {

    /**
     * 问卷ID
     */
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
     * 状态
     */
    private String status;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 已提交人数
     */
    private Integer submittedCount;

    /**
     * 目标学生数量
     */
    private Integer targetStudentCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
}
