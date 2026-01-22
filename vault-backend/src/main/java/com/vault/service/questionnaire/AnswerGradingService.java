package com.vault.service.questionnaire;

import com.vault.entity.mysql.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 答案评分服务
 */
@Slf4j
@Service
public class AnswerGradingService {

    /**
     * 评分答案
     */
    public GradingResult gradeAnswer(Question question, String studentAnswer) {
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            return new GradingResult(false, BigDecimal.ZERO, "未作答");
        }

        switch (question.getType()) {
            case "SINGLE_CHOICE":
            case "TRUE_FALSE":
                return gradeSingleChoice(question, studentAnswer);
            case "MULTIPLE_CHOICE":
                return gradeMultipleChoice(question, studentAnswer);
            case "SHORT_ANSWER":
                return gradeShortAnswer(question, studentAnswer);
            case "CODING":
                return gradeCoding(question, studentAnswer);
            default:
                return new GradingResult(false, BigDecimal.ZERO, "未知题型");
        }
    }

    /**
     * 单选题/判断题评分
     */
    private GradingResult gradeSingleChoice(Question question, String studentAnswer) {
        String correctAnswer = question.getCorrectAnswer();
        boolean isCorrect = correctAnswer.equalsIgnoreCase(studentAnswer.trim());
        BigDecimal score = isCorrect ? question.getScore() : BigDecimal.ZERO;
        String feedback = isCorrect ? "回答正确" : "回答错误";
        return new GradingResult(isCorrect, score, feedback);
    }

    /**
     * 多选题评分
     */
    private GradingResult gradeMultipleChoice(Question question, String studentAnswer) {
        String correctAnswer = question.getCorrectAnswer();

        Set<String> correctSet = new HashSet<>(Arrays.asList(correctAnswer.split(",")));
        Set<String> studentSet = new HashSet<>(Arrays.asList(studentAnswer.split(",")));

        boolean isCorrect = correctSet.equals(studentSet);
        BigDecimal score = isCorrect ? question.getScore() : BigDecimal.ZERO;
        String feedback = isCorrect ? "回答正确" : "回答错误";

        return new GradingResult(isCorrect, score, feedback);
    }

    /**
     * 简答题评分（简化版，实际应调用AI）
     */
    private GradingResult gradeShortAnswer(Question question, String studentAnswer) {
        String correctAnswer = question.getCorrectAnswer();

        // 简化评分：检查关键词
        String[] keywords = correctAnswer.split("[,，、]");
        int matchCount = 0;
        for (String keyword : keywords) {
            if (studentAnswer.contains(keyword.trim())) {
                matchCount++;
            }
        }

        double ratio = (double) matchCount / keywords.length;
        BigDecimal score = question.getScore().multiply(BigDecimal.valueOf(ratio));

        String feedback = String.format("匹配到 %d/%d 个关键点", matchCount, keywords.length);
        boolean isCorrect = ratio >= 0.6;

        return new GradingResult(isCorrect, score, feedback);
    }

    /**
     * 编程题评分（简化版，实际应运行测试用例）
     */
    private GradingResult gradeCoding(Question question, String studentAnswer) {
        // 简化评分：检查代码是否包含关键结构
        boolean hasCode = studentAnswer.length() > 10;
        BigDecimal score = hasCode ? question.getScore().multiply(BigDecimal.valueOf(0.5)) : BigDecimal.ZERO;
        String feedback = hasCode ? "代码已提交，需人工审核" : "代码过短";

        return new GradingResult(hasCode, score, feedback);
    }

    /**
     * 评分结果
     */
    public static class GradingResult {
        private final boolean isCorrect;
        private final BigDecimal score;
        private final String feedback;

        public GradingResult(boolean isCorrect, BigDecimal score, String feedback) {
            this.isCorrect = isCorrect;
            this.score = score;
            this.feedback = feedback;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public BigDecimal getScore() {
            return score;
        }

        public String getFeedback() {
            return feedback;
        }
    }
}
