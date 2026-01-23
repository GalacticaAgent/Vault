package com.vault.service.student;

import com.vault.dto.request.CreateCardRequest;
import com.vault.dto.response.DashboardCardResponse;
import com.vault.dto.response.DashboardResponse;

import java.util.List;

/**
 * 学生看板服务接口
 * <p>
 * 提供学生看板的所有数据查询功能
 *
 * @author Vault Team
 * @since 2026-01-22
 */
public interface DashboardService {

    /**
     * 获取学生看板的完整数据
     * <p>
     * 包含：
     * 1. 基本统计信息（学号、专业、提问总数、总分数）
     * 2. 问卷统计（已完成、待完成、完成率、平均用时）
     * 3. 成绩分析（平均分、最高分、最低分、成绩趋势、分数分布）
     * 4. 排名信息（班级排名、排名趋势）
     * 5. 知识点掌握进度
     * 6. 薄弱知识点列表
     * 7. 推荐学习资料
     * 8. 近期活动
     * 9. 自定义卡片列表
     *
     * @param userId 当前登录用户ID（从Spring Security上下文获取）
     * @param timeRange 时间范围：week-本周, month-本月
     * @return DashboardResponse 看板数据
     */
    DashboardResponse getDashboardData(Long userId, String timeRange);

    /**
     * 添加自定义看板卡片
     *
     * @param userId 当前登录用户ID
     * @param request 卡片请求数据
     * @return 新创建的卡片ID
     */
    Long addDashboardCard(Long userId, CreateCardRequest request);

    /**
     * 获取用户的所有自定义卡片
     *
     * @param userId 当前登录用户ID
     * @return 卡片列表
     */
    List<DashboardCardResponse> getDashboardCards(Long userId);

    /**
     * 删除自定义看板卡片
     *
     * @param userId 当前登录用户ID
     * @param cardId 卡片ID
     */
    void deleteDashboardCard(Long userId, Long cardId);

    /**
     * 更新自定义看板卡片
     *
     * @param userId 当前登录用户ID
     * @param cardId 卡片ID
     * @param request 更新的卡片数据
     */
    void updateDashboardCard(Long userId, Long cardId, CreateCardRequest request);

    /**
     * 刷新卡片数据
     *
     * @param userId 当前登录用户ID
     * @param cardId 卡片ID
     * @return 刷新后的卡片数据
     */
    DashboardCardResponse refreshDashboardCard(Long userId, Long cardId);
}
