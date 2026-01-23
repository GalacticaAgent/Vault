package com.vault.controller.student;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.common.Result;
import com.vault.dto.request.SubmitQuestionnaireRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.dto.response.QuestionnaireListResponse;
import com.vault.entity.mysql.Student;
import com.vault.mapper.StudentMapper;
import com.vault.service.questionnaire.QuestionnaireService;
import com.vault.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生问卷控制器
 * 
 * 提供学生查看问卷列表、答题、提交、查看成绩等功能。
 * 
 * 核心功能（参考《功能详细说明文档.md》4.2节）：
 * 1. 查看问卷列表：区分"待完成"和"已完成"
 * 2. 答题功能：
 *    - 获取问卷详情和题目内容
 *    - 逐题作答，支持前后翻题
 *    - 倒计时提醒
 *    - 题目导航（快速跳转到任意题目）
 *    - 保存答题进度（防止浏览器崩溃）
 * 3. 提交答案：确认提交，自动批改
 * 4. 查看结果：
 *    - 显示总分和每题得分
 *    - 显示错题和正确答案
 *    - AI 给出错题分析
 * 5. 答题记录：记录答题时间、修改次数等
 */
@Slf4j
@Tag(name = "学生问卷管理", description = "学生问卷答题相关接口")
@RestController
@RequestMapping("/student/questionnaire")
@RequiredArgsConstructor
public class StudentQuestionnaireController {
    
    private final QuestionnaireService questionnaireService;
    private final StudentMapper studentMapper;
    
    /**
     * 获取待完成的问卷列表
     * 
     * 显示学生尚未提交的问卷，按截止时间排序。
     */
    @Operation(summary = "获取待完成问卷", description = "获取当前学生待完成的问卷列表，按截止时间排序")
    @GetMapping("/pending")
    public Result<List<QuestionnaireListResponse>> getPendingQuestionnaires(
            @Parameter(description = "课程ID，可选") @RequestParam(required = false) Long courseId) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 获取学生ID
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student student = studentMapper.selectOne(wrapper);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        
        log.info("学生 {} 获取待完成问卷，课程ID：{}", student.getId(), courseId);
        
        List<QuestionnaireListResponse> questionnaires = questionnaireService.getPendingQuestionnaires(student.getId());
        return Result.success(questionnaires);
    }
    
    /**
     * 获取已完成的问卷列表
     * 
     * 显示学生已提交的问卷，可查看成绩。
     */
    @Operation(summary = "获取已完成问卷", description = "获取当前学生已完成的问卷列表")
    @GetMapping("/completed")
    public Result<List<QuestionnaireListResponse>> getCompletedQuestionnaires(
            @Parameter(description = "课程ID，可选") @RequestParam(required = false) Long courseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 获取学生ID
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student student = studentMapper.selectOne(wrapper);
        if (student == null) {
            return Result.error("学生信息不存在");
        }
        
        log.info("学生 {} 获取已完成问卷，课程ID：{}，页码：{}", student.getId(), courseId, page);
        
        List<QuestionnaireListResponse> questionnaires = questionnaireService.getCompletedQuestionnaires(student.getId());
        return Result.success(questionnaires);
    }
    
    /**
     * 获取问卷详情（用于答题）
     * 
     * 返回问卷的所有题目，学生可以开始答题。
     * 参考《功能详细说明文档.md》4.3节学生答题流程。
     */
    @Operation(summary = "获取问卷详情", description = "获取问卷的题目内容，用于答题")
    @GetMapping("/{questionnaireId}")
    public Result<QuestionnaireDetailResponse> getQuestionnaireForAnswer(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 获取问卷 {} 详情用于答题", studentId, questionnaireId);
        
        QuestionnaireDetailResponse questionnaire = questionnaireService.getQuestionnaireDetail(questionnaireId, studentId);
        return Result.success(questionnaire);
    }
    
    /**
     * 提交问卷答案
     * 
     * 学生完成答题后提交所有答案，系统自动批改。
     * 
     * 自动批改流程（参考《功能详细说明文档.md》4.3节）：
     * 1. 客观题（单选、多选、判断）：直接对比答案
     * 2. 简答题：调用AI进行语义分析
     * 3. 编程题：在沙箱环境中运行测试用例
     * 4. 计算总分并保存
     * 5. 异步更新学生肖像（分析薄弱知识点）
     */
    @Operation(summary = "提交问卷答案", description = "学生提交问卷的所有答案，系统自动批改")
    @PostMapping("/{questionnaireId}/submit")
    public Result<Object> submitQuestionnaire(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId,
            @Valid @RequestBody SubmitQuestionnaireRequest request) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 提交问卷 {}，答案数量：{}", studentId, questionnaireId, 
                request.getAnswers() != null ? request.getAnswers().size() : 0);
        
        // TODO: 实现提交问卷逻辑
        // SubmissionResult result = questionnaireService.submitQuestionnaire(studentId, questionnaireId, request);
        return Result.success("提交成功，正在批改中...", null);
    }
    
    /**
     * 保存答题进度
     * 
     * 防止浏览器崩溃导致答案丢失，定期保存学生的答题进度。
     * 前端可以每30秒或每答完一题后调用此接口。
     */
    @Operation(summary = "保存答题进度", description = "保存学生的答题进度，防止数据丢失")
    @PostMapping("/{questionnaireId}/progress")
    public Result<String> saveProgress(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId,
            @RequestBody SubmitQuestionnaireRequest request) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 保存问卷 {} 的答题进度", studentId, questionnaireId);
        
        // TODO: 实现保存进度逻辑（存入Redis，设置过期时间）
        // questionnaireService.saveProgress(studentId, questionnaireId, request);
        return Result.success("进度已保存", "保存成功");
    }
    
    /**
     * 获取答题进度
     * 
     * 学生重新打开问卷时，恢复之前保存的答题进度。
     */
    @Operation(summary = "获取答题进度", description = "获取学生保存的答题进度")
    @GetMapping("/{questionnaireId}/progress")
    public Result<Object> getProgress(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 获取问卷 {} 的答题进度", studentId, questionnaireId);
        
        // TODO: 实现获取进度逻辑（从Redis读取）
        // ProgressResponse progress = questionnaireService.getProgress(studentId, questionnaireId);
        return Result.success(null);
    }
    
    /**
     * 获取问卷成绩
     * 
     * 查看已提交问卷的总分和基本信息。
     */
    @Operation(summary = "获取问卷成绩", description = "获取指定问卷的成绩（总分）")
    @GetMapping("/{questionnaireId}/score")
    public Result<Object> getQuestionnaireScore(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 获取问卷 {} 成绩", studentId, questionnaireId);
        
        // TODO: 实现获取问卷成绩逻辑
        // ScoreResponse score = questionnaireService.getQuestionnaireScore(studentId, questionnaireId);
        return Result.success(null);
    }
    
    /**
     * 获取问卷详细报告
     * 
     * 查看每题得分、用时、正确答案等详细信息。
     * 包括AI生成的错题分析和学习建议。
     */
    @Operation(summary = "获取成绩报告", description = "获取问卷的详细成绩报告，包括每题得分、用时、错题分析等")
    @GetMapping("/{questionnaireId}/report")
    public Result<Object> getQuestionnaireReport(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 获取问卷 {} 的详细报告", studentId, questionnaireId);
        
        // TODO: 实现获取详细报告逻辑
        // QuestionnaireReportResponse report = questionnaireService.getQuestionnaireReport(studentId, questionnaireId);
        return Result.success(null);
    }
    
    /**
     * 获取错题分析
     * 
     * 查看做错的题目，包括：
     * - 错题和正确答案
     * - AI给出的错题分析
     * - 相关知识点讲解
     * - 推荐的学习资料
     */
    @Operation(summary = "获取错题分析", description = "获取问卷的错题详情和AI分析")
    @GetMapping("/{questionnaireId}/mistakes")
    public Result<List<Object>> getMistakeAnalysis(
            @Parameter(description = "问卷ID") @PathVariable Long questionnaireId) {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 获取问卷 {} 的错题分析", studentId, questionnaireId);
        
        // TODO: 实现获取错题分析逻辑
        // List<MistakeAnalysis> mistakes = questionnaireService.getMistakeAnalysis(studentId, questionnaireId);
        return Result.success(List.of());
    }
}
