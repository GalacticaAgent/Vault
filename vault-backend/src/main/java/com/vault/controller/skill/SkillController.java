package com.vault.controller.skill;

import com.vault.common.Result;
import com.vault.dto.request.ExecuteSkillRequest;
import com.vault.service.skill.SkillDefinition;
import com.vault.service.skill.SkillExecutor;
import com.vault.service.skill.SkillLoader;
import com.vault.service.skill.SkillResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "技能管理", description = "AI技能相关接口")
@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {
    
    private final SkillLoader skillLoader;
    private final SkillExecutor skillExecutor;
    
    @Operation(summary = "获取技能列表", description = "获取所有可用的AI技能")
    @GetMapping("/list")
    public Result<List<SkillDefinition>> listSkills() {
        log.info("获取技能列表");
        List<SkillDefinition> skills = skillLoader.getAllSkills();
        return Result.success(skills);
    }
    
    @Operation(summary = "获取技能详情", description = "获取指定技能的详细信息")
    @GetMapping("/{skillName}")
    public Result<SkillDefinition> getSkillDetail(
            @Parameter(description = "技能名称") @PathVariable String skillName) {
        
        log.info("获取技能详情: {}", skillName);
        SkillDefinition skill = skillLoader.getSkill(skillName);
        
        if (skill == null) {
            return Result.error("技能不存在");
        }
        
        return Result.success(skill);
    }
    
    @Operation(summary = "执行技能", description = "执行指定的AI技能")
    @PostMapping("/{skillName}/execute")
    public Result<SkillResult> executeSkill(
            @Parameter(description = "技能名称") @PathVariable String skillName,
            @Valid @RequestBody ExecuteSkillRequest request) {
        
        log.info("执行技能: {}", skillName);
        
        SkillDefinition skill = skillLoader.getSkill(skillName);
        if (skill == null) {
            return Result.error("技能不存在");
        }
        
        // TODO: 创建 SkillContext 并执行技能
        // SkillContext context = new SkillContext(request.getParams());
        // SkillResult result = skillExecutor.execute(skill, context);
        log.warn("技能执行功能待实现");
        return Result.error("技能执行功能待实现");
    }
    
    @Operation(summary = "重新加载技能", description = "重新加载所有技能定义")
    @PostMapping("/reload")
    public Result<Integer> reloadSkills() {
        log.info("重新加载技能");
        int count = skillLoader.reloadSkills();
        return Result.success(count);
    }
    
    @Operation(summary = "搜索技能", description = "根据关键词搜索技能")
    @GetMapping("/search")
    public Result<List<SkillDefinition>> searchSkills(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        log.info("搜索技能: {}", keyword);
        List<SkillDefinition> skills = skillLoader.searchSkills(keyword);
        return Result.success(skills);
    }
}
