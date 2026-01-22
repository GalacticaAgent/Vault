package com.vault.service.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Locale;

public class QueryIntentParser {
    @Getter
    public enum QueryType {
        AVG_SCORE,
        QUESTIONNAIRE_RATE,
        ACTIVE_STUDENTS,
        CLASS_COMPARE,
        STUDENT_DETAIL,
        UNKNOWN
    }

    @Data
    @AllArgsConstructor
    public static class Intent {
        private QueryType type;
        private String className;
        private Long studentId;
        private Integer topN;
    }

    public Intent parse(String query) {
        if (query == null || query.isBlank()) return new Intent(QueryType.UNKNOWN, null, null, null);
        String q = query.toLowerCase(Locale.ROOT);
        if (q.contains("平均分") || q.contains("avg") || q.contains("score")) {
            return new Intent(QueryType.AVG_SCORE, extractClassName(q), null, null);
        }
        if (q.contains("问卷") && (q.contains("完成") || q.contains("提交") || q.contains("rate"))) {
            return new Intent(QueryType.QUESTIONNAIRE_RATE, extractClassName(q), null, null);
        }
        if (q.contains("活跃") || q.contains("active")) {
            return new Intent(QueryType.ACTIVE_STUDENTS, extractClassName(q), null, null);
        }
        if (q.contains("对比") || q.contains("compare")) {
            return new Intent(QueryType.CLASS_COMPARE, null, null, extractTopN(q));
        }
        if (q.contains("学生") && (q.contains("详情") || q.contains("detail") || q.contains("profile"))) {
            Long sid = extractStudentId(q);
            return new Intent(QueryType.STUDENT_DETAIL, null, sid, null);
        }
        return new Intent(QueryType.UNKNOWN, null, null, null);
    }

    private String extractClassName(String q) {
        int idx = q.indexOf("班");
        if (idx >= 0 && idx + 1 < q.length()) {
            return q.substring(0, Math.min(q.length(), idx + 3));
        }
        return null;
    }

    private Integer extractTopN(String q) {
        for (int i = 1; i <= 10; i++) {
            if (q.contains("top" + i) || q.contains("前" + i)) return i;
        }
        return null;
    }

    private Long extractStudentId(String q) {
        String digits = q.replaceAll("\\D+", "");
        if (digits.isBlank()) return null;
        try {
            return Long.parseLong(digits);
        } catch (Exception e) {
            return null;
        }
    }
}
