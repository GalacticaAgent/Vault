package com.vault.dto.response;

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
}
