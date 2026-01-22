package com.vault.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷详情响应DTO
 */
@Data
public class QuestionnaireDetailResponse {

    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private BigDecimal totalScore;
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Long id;
        private Integer questionOrder;
        private String type;
        private String content;
        private List<String> options;
        private String knowledgePoint;
        private String difficulty;
        private BigDecimal score;
    }
}
