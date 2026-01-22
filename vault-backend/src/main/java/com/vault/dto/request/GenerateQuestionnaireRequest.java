package com.vault.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * AI 生成问卷请求 DTO（预留接口）
 */
@Data
public class GenerateQuestionnaireRequest {

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
     * 知识点列表
     */
    @NotEmpty(message = "知识点列表不能为空")
    private List<String> knowledgePoints;

    /**
     * 难度等级
     */
    private String difficulty;

    /**
     * 题目数量
     */
    @NotNull(message = "题目数量不能为空")
    @Min(value = 1, message = "至少生成1道题")
    private Integer questionCount;

    /**
     * 目标学生ID列表（用于针对性生成）
     */
    private List<Long> targetStudents;
}
