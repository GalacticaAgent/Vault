package com.vault.controller.teacher;

import com.vault.common.Result;
import com.vault.dto.request.CreateQuestionnaireRequest;
import com.vault.dto.request.GenerateQuestionnaireRequest;
import com.vault.dto.request.UpdateQuestionnaireRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.dto.response.QuestionnaireListResponse;
import com.vault.service.questionnaire.QuestionGeneratorService;
import com.vault.service.questionnaire.QuestionnaireService;
import com.vault.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师问卷管理控制器
 */
@Tag(name = "教师问卷管理", description = "教师端问卷创建、管理相关接口")
@RestController
@RequestMapping("/teacher/questionnaires")
@RequiredArgsConstructor
public class TeacherQuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final QuestionGeneratorService questionGeneratorService;

    /**
     * 创建问卷
     */
    @Operation(summary = "创建问卷")
    @PostMapping
    public Result<QuestionnaireDetailResponse> createQuestionnaire(
            @Valid @RequestBody CreateQuestionnaireRequest request) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        QuestionnaireDetailResponse response = questionnaireService.createQuestionnaire(teacherId, request);
        return Result.success("问卷创建成功", response);
    }

    /**
     * 获取问卷列表
     */
    @Operation(summary = "获取问卷列表")
    @GetMapping
    public Result<List<QuestionnaireListResponse>> getQuestionnaireList(
            @RequestParam(required = false) String status) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        List<QuestionnaireListResponse> list = questionnaireService.getQuestionnaireList(teacherId, status);
        return Result.success(list);
    }

    /**
     * 获取问卷详情
     */
    @Operation(summary = "获取问卷详情")
    @GetMapping("/{id}")
    public Result<QuestionnaireDetailResponse> getQuestionnaireDetail(@PathVariable Long id) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        QuestionnaireDetailResponse response = questionnaireService.getQuestionnaireDetail(id, teacherId);
        return Result.success(response);
    }

    /**
     * 更新问卷
     */
    @Operation(summary = "更新问卷")
    @PutMapping
    public Result<QuestionnaireDetailResponse> updateQuestionnaire(
            @Valid @RequestBody UpdateQuestionnaireRequest request) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        QuestionnaireDetailResponse response = questionnaireService.updateQuestionnaire(teacherId, request);
        return Result.success("问卷更新成功", response);
    }

    /**
     * 发布问卷
     */
    @Operation(summary = "发布问卷")
    @PostMapping("/{id}/publish")
    public Result<String> publishQuestionnaire(@PathVariable Long id) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        questionnaireService.publishQuestionnaire(id, teacherId);
        return Result.success("问卷已发布");
    }

    /**
     * 关闭问卷
     */
    @Operation(summary = "关闭问卷")
    @PostMapping("/{id}/close")
    public Result<String> closeQuestionnaire(@PathVariable Long id) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        questionnaireService.closeQuestionnaire(id, teacherId);
        return Result.success("问卷已关闭");
    }

    /**
     * 删除问卷
     */
    @Operation(summary = "删除问卷")
    @DeleteMapping("/{id}")
    public Result<String> deleteQuestionnaire(@PathVariable Long id) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        questionnaireService.deleteQuestionnaire(id, teacherId);
        return Result.success("问卷已删除");
    }

    /**
     * AI 生成问卷（预留接口）
     */
    @Operation(summary = "AI 生成问卷")
    @PostMapping("/generate")
    public Result<QuestionnaireDetailResponse> generateQuestionnaire(
            @Valid @RequestBody GenerateQuestionnaireRequest request) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        QuestionnaireDetailResponse response = questionGeneratorService.generateQuestionnaire(teacherId, request);
        return Result.success("问卷生成成功", response);
    }
}
