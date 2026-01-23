package com.vault.controller.teacher;

import com.vault.common.Result;
import com.vault.dto.request.CreateCardRequest;
import com.vault.dto.response.DashboardCardResponse;
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
 * 教师看板控制器
 * 
 * 教师看板是为教师设计的数据分析中心，让教师能够全面了解班级的学习情况。
 * 
 * 核心功能（参考《功能详细说明文档.md》5.2节）：
 * 1. 班级概览：显示班级基本信息（学生人数、平均成绩等）
 * 2. 固定数据卡片：班级平均分、问卷完成率、活跃学生、常见问题统计
 * 3. 自定义查询卡片（核心功能）：
 *    - "这个班最不了解进程概念的同学是谁？"
 *    - "这个班最活跃的五名同学是谁？"
 *    - "这个班代码风格最好的同学是谁？"
 *    - "这个班谁的代码和成绩最不匹配？"
 * 4. 学生列表：查看所有学生，可以点击查看详细情况
 * 5. 数据可视化：成绩分布直方图、知识点掌握雷达图、学生活跃度趋势图
 */
@Slf4j
@Tag(name = "教师看板管理", description = "教师班级数据统计与自定义查询卡片管理相关接口")
@RestController
@RequestMapping("/teacher/dashboard")
@RequiredArgsConstructor
public class TeacherDashboardController {
    
    // TODO: 注入 TeacherDashboardService
    // private final TeacherDashboardService teacherDashboardService;
    
    /**
     * 获取教师看板数据
     */
    @Operation(summary = "获取教师看板", description = "获取教师的班级统计和教学数据，包括固定卡片和自定义卡片")
    @GetMapping
    public Result<Object> getTeacherDashboard() {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("获取教师 {} 的看板数据", teacherId);
        
        // TODO: 实现获取教师看板数据逻辑
        // TeacherDashboardResponse response = teacherDashboardService.getDashboardData(teacherId);
        return Result.success(null);
    }
    
    /**
     * 添加自定义查询卡片
     * 
     * 教师可以用自然语言描述想要查询的数据，系统使用AI理解并生成卡片。
     * 示例查询（参考《vault.md》UI文档）：
     * - "这个班最不了解进程概念的同学是谁？"
     * - "这个班最活跃的五名同学是谁？"
     * - "这个班代码风格最好的同学是谁？"
     * - "这个班谁的代码和成绩最不匹配？"
     */
    @Operation(summary = "添加自定义查询卡片", description = "教师添加自定义数据查询卡片，支持自然语言描述")
    @PostMapping("/card")
    public Result<Long> addDashboardCard(@Valid @RequestBody CreateCardRequest request) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 添加自定义查询卡片：{}", teacherId, request.getTitle());
        
        // TODO: 实现添加卡片逻辑
        // Long cardId = teacherDashboardService.addDashboardCard(teacherId, request);
        return Result.success("添加卡片成功", 1L);
    }
    
    /**
     * 删除自定义查询卡片
     */
    @Operation(summary = "删除自定义查询卡片", description = "删除教师创建的自定义查询卡片")
    @DeleteMapping("/card/{id}")
    public Result<String> deleteDashboardCard(
            @Parameter(description = "卡片ID") @PathVariable Long id) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 删除卡片 {}", teacherId, id);
        
        // TODO: 实现删除卡片逻辑
        // teacherDashboardService.deleteDashboardCard(teacherId, id);
        return Result.success("删除卡片成功", "删除成功");
    }
    
    /**
     * 更新自定义查询卡片
     */
    @Operation(summary = "更新自定义查询卡片", description = "更新教师创建的自定义查询卡片信息")
    @PutMapping("/card/{id}")
    public Result<String> updateDashboardCard(
            @Parameter(description = "卡片ID") @PathVariable Long id,
            @Valid @RequestBody CreateCardRequest request) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 更新卡片 {}：{}", teacherId, id, request.getTitle());
        
        // TODO: 实现更新卡片逻辑
        // teacherDashboardService.updateDashboardCard(teacherId, id, request);
        return Result.success("更新卡片成功", "更新成功");
    }
    
    /**
     * 获取所有自定义卡片
     */
    @Operation(summary = "获取自定义卡片列表", description = "获取教师创建的所有自定义查询卡片")
    @GetMapping("/cards")
    public Result<List<DashboardCardResponse>> getDashboardCards() {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("获取教师 {} 的自定义卡片列表", teacherId);
        
        // TODO: 实现获取卡片列表逻辑
        // List<DashboardCardResponse> cards = teacherDashboardService.getDashboardCards(teacherId);
        return Result.success(List.of());
    }
    
    /**
     * 刷新卡片数据
     * 
     * 重新执行卡片的查询，获取最新数据。
     * 这些卡片会实时更新（相当于以一定时间间隔一直在问学生肖像）
     */
    @Operation(summary = "刷新卡片数据", description = "手动触发指定卡片的数据刷新")
    @PostMapping("/card/{id}/refresh")
    public Result<DashboardCardResponse> refreshDashboardCard(
            @Parameter(description = "卡片ID") @PathVariable Long id) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 刷新卡片 {}", teacherId, id);
        
        // TODO: 实现刷新卡片逻辑
        // DashboardCardResponse card = teacherDashboardService.refreshDashboardCard(teacherId, id);
        return Result.success("刷新成功", null);
    }
    
    /**
     * 获取教师的班级列表
     */
    @Operation(summary = "获取班级列表", description = "获取教师教授的所有班级")
    @GetMapping("/classes")
    public Result<List<Object>> getTeacherClasses() {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("获取教师 {} 的班级列表", teacherId);
        
        // TODO: 实现获取班级列表逻辑
        // List<ClassInfo> classes = teacherDashboardService.getTeacherClasses(teacherId);
        return Result.success(List.of());
    }
    
    /**
     * 获取班级学生列表
     */
    @Operation(summary = "获取班级学生列表", description = "获取指定班级的所有学生列表")
    @GetMapping("/class/{classId}/students")
    public Result<List<Object>> getClassStudents(
            @Parameter(description = "班级ID") @PathVariable Long classId) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 获取班级 {} 的学生列表", teacherId, classId);
        
        // TODO: 实现获取学生列表逻辑
        // List<StudentBasicInfo> students = teacherDashboardService.getClassStudents(teacherId, classId);
        return Result.success(List.of());
    }
    
    /**
     * 获取班级统计数据
     */
    @Operation(summary = "获取班级统计", description = "获取指定班级的统计数据")
    @GetMapping("/class/{classId}/statistics")
    public Result<Object> getClassStatistics(
            @Parameter(description = "班级ID") @PathVariable Long classId) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 获取班级 {} 的统计数据", teacherId, classId);
        
        // TODO: 实现获取班级统计逻辑
        // ClassStatisticsResponse statistics = teacherDashboardService.getClassStatistics(teacherId, classId);
        return Result.success(null);
    }
    
    /**
     * 获取学生详细报告
     */
    @Operation(summary = "获取学生报告", description = "获取指定学生的详细学习报告（学生肖像）")
    @GetMapping("/student/{studentId}/report")
    public Result<Object> getStudentReport(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 获取学生 {} 的学习报告", teacherId, studentId);
        
        // TODO: 实现获取学生报告逻辑
        // StudentReportResponse report = teacherDashboardService.getStudentReport(teacherId, studentId);
        return Result.success(null);
    }
    
    /**
     * 获取知识点掌握情况
     */
    @Operation(summary = "获取知识点掌握情况", description = "获取班级学生对各知识点的掌握情况")
    @GetMapping("/class/{classId}/knowledge")
    public Result<List<Object>> getKnowledgeStatistics(
            @Parameter(description = "班级ID") @PathVariable Long classId) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 获取班级 {} 的知识点掌握情况", teacherId, classId);
        
        // TODO: 实现获取知识点统计逻辑
        // List<KnowledgePointStatistics> statistics = teacherDashboardService.getKnowledgeStatistics(teacherId, classId);
        return Result.success(List.of());
    }
    
    /**
     * 班级对比（可选功能）
     */
    @Operation(summary = "班级对比", description = "对比教师教授的多个班级的数据")
    @GetMapping("/classes/compare")
    public Result<Object> compareClasses(
            @Parameter(description = "要对比的班级ID列表") @RequestParam List<Long> classIds) {
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 对比班级：{}", teacherId, classIds);
        
        // TODO: 实现班级对比逻辑
        // ClassComparisonResponse comparison = teacherDashboardService.compareClasses(teacherId, classIds);
        return Result.success(null);
    }
}
