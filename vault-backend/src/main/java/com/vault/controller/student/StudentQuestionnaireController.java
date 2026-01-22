package com.vault.controller.student;

import com.vault.common.Result;
import com.vault.dto.request.SubmitAnswersRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.dto.response.QuestionnaireListResponse;
import com.vault.dto.response.QuestionnaireResultResponse;
import com.vault.security.JwtUtil;
import com.vault.service.questionnaire.StudentQuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生问卷控制器
 */
@Tag(name = "学生问卷", description = "学生问卷相关接口")
@RestController
@RequestMapping("/student/questionnaire")
@RequiredArgsConstructor
public class StudentQuestionnaireController {

    private final StudentQuestionnaireService questionnaireService;
    private final JwtUtil jwtUtil;

    /**
     * 获取问卷列表
     */
    @Operation(summary = "获取问卷列表")
    @GetMapping("/list")
    public Result<List<QuestionnaireListResponse>> getQuestionnaireList(
            @Parameter(description = "状态：pending-待完成，completed-已完成，不传则返回全部")
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        List<QuestionnaireListResponse> list = questionnaireService.getQuestionnaireList(userId, status);
        return Result.success(list);
    }

    /**
     * 获取问卷详情
     */
    @Operation(summary = "获取问卷详情")
    @GetMapping("/{id}")
    public Result<QuestionnaireDetailResponse> getQuestionnaireDetail(
            @Parameter(description = "问卷ID") @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        QuestionnaireDetailResponse detail = questionnaireService.getQuestionnaireDetail(id, userId);
        return Result.success(detail);
    }

    /**
     * 提交答案
     */
    @Operation(summary = "提交答案")
    @PostMapping("/submit")
    public Result<Void> submitAnswers(
            @Valid @RequestBody SubmitAnswersRequest request,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        questionnaireService.submitAnswers(request, userId);
        return Result.success("提交成功", null);
    }

    /**
     * 获取问卷结果
     */
    @Operation(summary = "获取问卷结果")
    @GetMapping("/{id}/result")
    public Result<QuestionnaireResultResponse> getQuestionnaireResult(
            @Parameter(description = "问卷ID") @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long userId = getUserIdFromToken(token);
        QuestionnaireResultResponse result = questionnaireService.getQuestionnaireResult(id, userId);
        return Result.success(result);
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}
