package com.vault.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 更新问卷请求 DTO
 */
@Data
public class UpdateQuestionnaireRequest {

    /**
     * 问卷ID
     */
    @NotNull(message = "问卷ID不能为空")
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
     * 时间限制（分钟）
     */
    private Integer timeLimit;

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
     * 目标学生ID列表
     */
    private List<Long> targetStudents;
}
