package com.vault.dto.response;

import com.vault.entity.mysql.DashboardCard;
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
public class TeacherDashboardResponse {

    private TeacherInfo teacher;
    private List<ClassItem> classes;
    private FixedCards fixedCards;
    private List<StudentItem> students;
    private List<DashboardCard> cards;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherInfo {
        private Long id;
        private String teacherNumber;
        private String department;
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassItem {
        private String className;
        private Integer studentCount;
        private BigDecimal avgScore;
        private BigDecimal questionnaireCompletionRate;
        private Integer activeStudents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixedCards {
        private BigDecimal avgScore;
        private BigDecimal questionnaireCompletionRate;
        private Integer activeStudents;
        private List<IssueItem> commonIssuesTopN;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssueItem {
        private String issue;
        private Integer count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentItem {
        private Long id;
        private String username;
        private String className;
        private BigDecimal totalScores;
    }
}
