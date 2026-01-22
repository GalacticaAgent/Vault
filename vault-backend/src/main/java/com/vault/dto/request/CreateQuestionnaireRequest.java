package com.vault.dto.request;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建问卷请求 DTO
 */
@Data
public class CreateQuestionnaireRequest {

    /**
     * 问卷标题
     */
    @NotBlank(message = "问卷标题不能为空")
    private String title;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 时间限制（分钟）
     */
    @NotNull(message = "时间限制不能为空")
    @Min(value = 1, message = "时间限制至少1分钟")
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
     * 目标学生ID列表（空表示全部学生）
     */
    private List<Long> targetStudents;

    /**
     * 题目列表
     */
    @NotEmpty(message = "题目列表不能为空")
    @Valid
    private List<QuestionDTO> questions;

    /**
     * 题目数据传输对象
     */
    @Data
    public static class QuestionDTO {

        /**
         * 题目类型
         */
        @NotBlank(message = "题目类型不能为空")
        private String type;

        /**
         * 题目内容
         */
        @NotBlank(message = "题目内容不能为空")
        private String content;

        /**
         * 选项列表
         */
        private List<String> options;

        /**
         * 正确答案
         */
        @NotBlank(message = "正确答案不能为空")
        private String correctAnswer;

        /**
         * 关联知识点
         */
        private String knowledgePoint;

        /**
         * 难度
         */
        private String difficulty;

        /**
         * 题目分值
         */
        @NotNull(message = "题目分值不能为空")
        @DecimalMin(value = "0.0", message = "分值不能为负数")
        private BigDecimal score;

        /**
         * 题目解析
         */
        private String explanation;
    }
}
