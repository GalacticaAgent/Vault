package com.vault.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷结果响应DTO
 */
@Data
public class QuestionnaireResultResponse {

    private Long id;
    private String title;
    private BigDecimal totalScore;
    private BigDecimal myScore;
    private BigDecimal passScore;
    private Boolean isPassed;
    private Integer timeSpent;
    private LocalDateTime submitTime;
    private List<QuestionResultDTO> questionResults;

    @Data
    public static class QuestionResultDTO {
        private Long questionId;
        private Integer questionOrder;
        private String type;
        private String content;
        private List<String> options;
        private String myAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private BigDecimal score;
        private BigDecimal maxScore;
        private String feedback;
        private String explanation;
        private String knowledgePoint;
    }
}
