package com.vault.model.skill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由决策模型 - 对标 chat-skills 的 RoutingDecision
 * 用于表示LLM的Skill路由决策结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutingDecision {
    
    /**
     * 是否使用Skills处理此查询
     */
    @JsonProperty("use_skills")
    @Builder.Default
    private Boolean useSkills = false;
    
    /**
     * 选中的Skill名称列表（0到N个）
     */
    @JsonProperty("selected_skills")
    @Builder.Default
    private List<String> selectedSkills = new ArrayList<>();
    
    /**
     * 决策理由（一句话说明）
     */
    @JsonProperty("rationale")
    private String rationale;
    
    /**
     * 创建不使用Skill的默认决策
     */
    public static RoutingDecision noSkills(String rationale) {
        return RoutingDecision.builder()
                .useSkills(false)
                .selectedSkills(new ArrayList<>())
                .rationale(rationale)
                .build();
    }
    
    /**
     * 创建使用Skill的决策
     */
    public static RoutingDecision withSkills(List<String> skills, String rationale) {
        return RoutingDecision.builder()
                .useSkills(true)
                .selectedSkills(skills != null ? skills : new ArrayList<>())
                .rationale(rationale)
                .build();
    }
    
    /**
     * 验证决策是否有效
     */
    public boolean isValid() {
        if (useSkills == null) {
            return false;
        }
        if (useSkills && (selectedSkills == null || selectedSkills.isEmpty())) {
            return false;
        }
        if (rationale == null || rationale.trim().isEmpty()) {
            return false;
        }
        return true;
    }
    
    /**
     * 截断过长的理由
     */
    public void truncateRationale(int maxLength) {
        if (rationale != null && rationale.length() > maxLength) {
            rationale = rationale.substring(0, maxLength - 3) + "...";
        }
    }
}
