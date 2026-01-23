package com.vault.controller.student;

import com.vault.common.Result;
import com.vault.dto.response.MaterialResponse;
import com.vault.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生材料控制器
 * 提供学生查看学习材料的功能
 */
@Slf4j
@Tag(name = "学生材料管理", description = "学生学习材料相关接口")
@RestController
@RequestMapping("/student/material")
@RequiredArgsConstructor
public class StudentMaterialController {
    
    /**
     * 获取推荐材料列表
     */
    @Operation(summary = "获取推荐材料", description = "获取为当前学生推荐的学习材料")
    @GetMapping("/recommended")
    public Result<List<MaterialResponse>> getRecommendedMaterials() {
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 获取推荐材料", studentId);
        
        // TODO: 实现获取推荐材料逻辑
        return Result.success(List.of());
    }
    
    /**
     * 获取课程材料列表
     */
    @Operation(summary = "获取课程材料", description = "获取指定课程的学习材料")
    @GetMapping("/course/{courseId}")
    public Result<List<MaterialResponse>> getCourseMaterials(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("获取课程 {} 的材料", courseId);
        
        // TODO: 实现获取课程材料逻辑
        return Result.success(List.of());
    }
    
    /**
     * 记录材料学习记录
     */
    @Operation(summary = "记录学习记录", description = "记录学生学习材料的时长和进度")
    @PostMapping("/{materialId}/record")
    public Result<String> recordLearning(
            @Parameter(description = "材料ID") @PathVariable Long materialId,
            @Parameter(description = "学习时长（秒）") @RequestParam Integer duration) {
        
        Long studentId = SecurityUtil.getCurrentUserId();
        log.info("学生 {} 学习材料 {}，时长: {} 秒", studentId, materialId, duration);
        
        // TODO: 实现记录学习记录逻辑
        return Result.success("学习记录已保存", "记录成功");
    }
}
