package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自定义卡片实体类
 */
@Data
@TableName("dashboard_cards")
public class DashboardCard {

    /**
     * 卡片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 刷新间隔（毫秒，0表示不自动刷新）
     */
    private Integer refreshInterval;

    /**
     * 卡片顺序
     */
    private Integer cardOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
