package com.vault.controller.teacher;

import com.vault.common.Result;
import com.vault.dto.response.MaterialResponse;
import com.vault.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教师材料控制器
 * 提供教师管理学习材料的功能
 */
@Slf4j
@Tag(name = "教师材料管理", description = "教师学习材料管理相关接口")
@RestController
@RequestMapping("/teacher/material")
@RequiredArgsConstructor
public class TeacherMaterialController {
    
    /**
     * 为课程上传材料
     */
    @Operation(summary = "上传课程材料", description = "为指定课程上传学习材料")
    @PostMapping("/upload")
    public Result<MaterialResponse> uploadCourseMaterial(
            @Parameter(description = "课程ID") @RequestParam Long courseId,
            @Parameter(description = "材料文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "材料名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "材料描述") @RequestParam(value = "description", required = false) String description) {
        
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 为课程 {} 上传材料: {}", teacherId, courseId, file.getOriginalFilename());
        
        // TODO: 实现教师上传课程材料逻辑
        MaterialResponse response = MaterialResponse.builder()
                .id(1L)
                .name(name != null ? name : file.getOriginalFilename())
                .description(description)
                .fileUrl("/uploads/" + file.getOriginalFilename())
                .build();
        
        return Result.success("材料上传成功", response);
    }
    
    /**
     * 获取课程材料列表
     */
    @Operation(summary = "获取课程材料列表", description = "获取指定课程的所有学习材料")
    @GetMapping("/course/{courseId}")
    public Result<List<MaterialResponse>> getCourseMaterials(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        
        log.info("获取课程 {} 的材料列表", courseId);
        
        // TODO: 实现获取课程材料列表逻辑
        return Result.success(List.of());
    }
    
    /**
     * 删除课程材料
     */
    @Operation(summary = "删除课程材料", description = "删除指定的课程材料")
    @DeleteMapping("/{materialId}")
    public Result<String> deleteMaterial(
            @Parameter(description = "材料ID") @PathVariable Long materialId) {
        
        Long teacherId = SecurityUtil.getCurrentUserId();
        log.info("教师 {} 删除材料 {}", teacherId, materialId);
        
        // TODO: 实现删除材料逻辑
        return Result.success("材料删除成功", "删除成功");
    }
    
    /**
     * 获取材料使用统计
     */
    @Operation(summary = "获取材料统计", description = "获取材料的学习统计数据")
    @GetMapping("/{materialId}/statistics")
    public Result<Object> getMaterialStatistics(
            @Parameter(description = "材料ID") @PathVariable Long materialId) {
        
        log.info("获取材料 {} 的统计数据", materialId);
        
        // TODO: 实现获取材料统计逻辑
        return Result.success(null);
    }
}
