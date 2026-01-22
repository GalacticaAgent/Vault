package com.vault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherStudentDetailResponse {
    private Long id;
    private String username;
    private String className;
    private BigDecimal totalScores;
    private Integer totalQuestions;
    private BigDecimal questionnaireCompletionRate;
    private Boolean activeIn7Days;
}
