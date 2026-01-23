package com.vault.controller.material;

import com.vault.common.Result;
import com.vault.dto.response.MaterialResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 材料控制器（公共查询接口）
 * 提供学习材料的查询、搜索等公共功能
 * 
 * 注意：材料的上传、删除等管理功能在 TeacherMaterialController
 */
@Slf4j
@Tag(name = "材料查询", description = "学习材料公共查询接口")
@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class MaterialController {
    
    /**
     * 获取公开材料列表
     */
    @Operation(summary = "获取材料列表", description = "获取所有公开的学习材料")
    @GetMapping("/list")
    public Result<List<MaterialResponse>> listMaterials(
            @Parameter(description = "材料类型") @RequestParam(value = "type", required = false) String type) {
        
        log.info("获取公开材料列表，类型：{}", type);
        
        // TODO: 实现材料列表查询逻辑（仅查询公开材料）
        return Result.success(List.of());
    }
    
    /**
     * 获取材料详情
     */
    @Operation(summary = "获取材料详情", description = "获取指定材料的详细信息")
    @GetMapping("/{materialId}")
    public Result<MaterialResponse> getMaterialDetail(
            @Parameter(description = "材料ID") @PathVariable Long materialId) {
        
        log.info("获取材料详情: {}", materialId);
        
        // TODO: 实现材料详情查询逻辑
        MaterialResponse response = MaterialResponse.builder()
                .id(materialId)
                .name("示例材料")
                .description("这是一个示例材料")
                .fileUrl("/uploads/example.pdf")
                .build();
        
        return Result.success(response);
    }
    
    /**
     * 搜索材料
     */
    @Operation(summary = "搜索材料", description = "根据关键词搜索公开的学习材料")
    @GetMapping("/search")
    public Result<List<MaterialResponse>> searchMaterials(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        log.info("搜索材料: {}", keyword);
        
        // TODO: 实现材料搜索逻辑（仅搜索公开材料）
        return Result.success(List.of());
    }
}
