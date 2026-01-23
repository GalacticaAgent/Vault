package com.vault.dto.response;

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
     * 时间限制（分钟）
     */
    private Integer timeLimit;

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
}
