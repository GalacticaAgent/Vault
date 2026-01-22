package com.vault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassCompareResponse {
    private List<Item> classes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String className;
        private Integer studentCount;
        private BigDecimal avgScore;
        private BigDecimal questionnaireCompletionRate;
        private Integer activeStudents;
    }
}
