package com.vault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 看板卡片响应DTO
 *
 * @author Vault Team
 * @since 2026-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCardResponse {

    /**
     * 卡片ID
     */
    private Long id;

    /**
     * 卡片标题
     */
    private String title;

    /**
     * 查询描述
     */
    private String query;

    /**
     * 卡片内容（JSON格式）
     */
    private String content;

    /**
     * 刷新间隔（毫秒）
     */
    private Integer refreshInterval;

    /**
     * 卡片顺序
     */
    private Integer cardOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
