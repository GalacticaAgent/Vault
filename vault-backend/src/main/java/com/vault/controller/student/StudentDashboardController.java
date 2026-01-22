package com.vault.controller.student;

import com.vault.common.Result;
import com.vault.dto.request.CreateCardRequest;
import com.vault.dto.response.DashboardCardResponse;
import com.vault.dto.response.DashboardResponse;
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
 * 学生看板控制器
 * <p>
 * 学生看板是一个可视化的数据展示页面，就像汽车的仪表盘一样，
 * 让学生一眼就能看到自己的学习情况和重要信息。
 *
 * 看板包含两部分：
 * 1. **固定卡片**：系统预定义的数据展示
 *    - "我的学号"：显示学生学号
 *    - "提问总数"：显示学生一共问了多少个问题
 *    - "问卷分数"：显示最近的问卷成绩
 *    - "班级排名"：显示在班级中的排名
 *    - "薄弱知识点"：显示掌握最不好的几个知识点
 *
 * 2. **自定义卡片**：学生自己添加的个性化数据展示
 *    学生可以用自然语言描述想要看到的数据，系统使用AI理解并生成卡片
 *    示例：
 *    - "我最薄弱的 5 个知识点"
 *    - "我最近一周的学习时长"
 *    - "我在进程管理方面的掌握程度"
 *    - "推荐给我的学习资料"
 *
 * 数据来源：
 * - MySQL: 学生基本信息、问卷成绩、答题记录
 * - Neo4j: 知识点掌握情况、薄弱知识点、资料推荐
 * - Redis: 排名、活跃度等缓存数据
 * - AI服务: 理解自定义查询、生成卡片内容
 *
 * @author Vault Team
 * @since 2026-01-21
 */
@Slf4j
@Tag(name = "学生看板管理", description = "学生个人看板数据统计与自定义卡片管理相关接口")
@RestController
@RequestMapping("/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final com.vault.service.student.DashboardService dashboardService;

    /**
     * 获取学生看板数据
     *
     * 该接口返回学生的综合看板数据，包括固定卡片数据和自定义卡片数据
     *
     * 【固定卡片数据】- 由系统自动计算和展示：
     * 1. 基本统计信息
     *    - 学号、专业、年级、班级
     *    - 总提问次数（从students表的total_questions字段）
     *    - 累计获得分数（从students表的total_scores字段）
     *
     * 2. 班级排名
     *    - 班级内排名（从Redis缓存获取，每天凌晨2点计算）
     *    - 排名趋势（与上次相比的变化）
     *
     * 3. 问卷统计
     *    - 已完成问卷数量（从questionnaire_submissions表统计）
     *    - 待完成问卷数量（从questionnaires表查询未提交的）
     *    - 完成率、平均用时
     *
     * 4. 成绩分析
     *    - 平均分、最高分、最低分（从questionnaire_submissions表）
     *    - 成绩趋势图数据（按时间排序的历史成绩）
     *    - 分数分布（各分数段的问卷数量）
     *
     * 5. 知识点掌握情况
     *    - 各知识点的掌握程度、正确率（从Neo4j知识图谱查询）
     *    - 查询语句示例：
     *      MATCH (s:Student {studentId: ?})-[m:MASTER]->(kp:KnowledgePoint)
     *      RETURN kp.name, m.proficiency, m.assessmentCount
     *
     * 6. 薄弱知识点
     *    - 掌握最不好的5个知识点（从Neo4j查询proficiency < 0.6的知识点）
     *    - 相关错题数量、最后评估时间、AI生成的学习建议
     *
     * 7. 推荐学习资料
     *    - 基于薄弱知识点推荐的资料（从Neo4j查询知识点-资料关系）
     *    - 查询语句示例：
     *      MATCH (s:Student)-[w:WEAK_AT]->(kp:KnowledgePoint)-[r:RELATE]->(m:Material)
     *      RETURN m ORDER BY r.relevance DESC, m.downloadCount DESC LIMIT 5
     *
     * 8. 近期活动
     *    - 最近提交的问卷、下载的资料、创建的对话等
     *
     * 【自定义卡片数据】- 由学生创建：
     * - 从dashboard_cards表查询学生创建的所有卡片
     * - 按card_order字段升序排列
     *
     * 实现流程（参考功能详细说明文档 2.3节）：
     * 1. 从Spring Security上下文获取当前登录的学生ID
     * 2. 从MySQL查询学生基本信息（学号、成绩等）
     * 3. 从Neo4j知识图谱查询学生的薄弱知识点
     * 4. 从Redis缓存中获取经常访问的数据（如排名）
     * 5. 将所有数据组装成固定卡片的格式
     * 6. 从dashboard_cards表查询自定义卡片
     * 7. 返回完整的看板数据给前端
     *
     * @return Result<DashboardResponse> 包含看板数据的统一响应结果
     */
    @Operation(
        summary = "获取学生看板数据",
        description = "获取学生的综合看板数据，包括统计信息、学习进度、成绩分析、近期活动和自定义卡片"
    )
    @GetMapping
    public Result<DashboardResponse> getDashboard() {
        try {
            log.info("开始获取学生看板数据");

            // 从Spring Security上下文获取当前登录的用户ID
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前登录用户ID: {}", userId);

            // 调用服务层获取看板数据
            DashboardResponse response = dashboardService.getDashboardData(userId);

            log.info("成功获取学生看板数据");
            return Result.success("获取看板数据成功", response);

        } catch (Exception e) {
            log.error("获取学生看板数据失败", e);
            return Result.error("获取看板数据失败：" + e.getMessage());
        }
    }

    /**
     * 添加自定义看板卡片
     *
     * 学生可以通过该接口添加自定义的数据展示卡片到个人看板中。
     * 学生使用自然语言描述想要看到的数据，系统使用AI理解并生成卡片。
     *
     * 卡片示例（参考功能详细说明文档 2.2节）：
     * - "我最薄弱的 5 个知识点"
     * - "我最近一周的学习时长"
     * - "我在进程管理方面的掌握程度"
     * - "推荐给我的学习资料"
     *
     * 实现流程（参考功能详细说明文档 2.3节）：
     * 1. 学生填写卡片信息
     *    - 卡片标题（例如："我的薄弱知识点"）
     *    - 查询描述（例如："列出我最薄弱的 5 个知识点"）
     *    - 刷新频率（多久更新一次数据，0表示不自动刷新）
     *    - 卡片顺序（控制显示位置）
     *
     * 2. 学生提交后，前端发送请求到后端
     *
     * 3. 后端将卡片配置保存到dashboard_cards表
     *    INSERT INTO dashboard_cards (user_id, title, query, refresh_interval, card_order)
     *    VALUES (?, ?, ?, ?, ?)
     *
     * 4. 后端使用AI（LLM）分析查询描述
     *    示例Prompt：
     *    ```
     *    用户查询：列出我最薄弱的 5 个知识点
     *
     *    请分析用户意图：
     *    1. 用户想查询什么数据？
     *    2. 应该从哪个数据源获取（MySQL/Neo4j/Redis）？
     *    3. 生成对应的查询语句（SQL/Cypher）
     *    ```
     *
     * 5. AI返回查询计划
     *    示例：
     *    ```
     *    {
     *      "dataSource": "Neo4j",
     *      "queryType": "weak_knowledge_points",
     *      "cypherQuery": "MATCH (s:Student {studentId: ?})-[m:MASTER]->(kp:KnowledgePoint)
     *                      WHERE m.proficiency < 0.6
     *                      RETURN kp.name, m.proficiency
     *                      ORDER BY m.proficiency ASC LIMIT 5"
     *    }
     *    ```
     *
     * 6. 执行查询，获取数据
     *
     * 7. AI将查询结果格式化成JSON
     *    示例：
     *    ```
     *    {
     *      "type": "weak_points",
     *      "data": [
     *        {"name": "虚拟内存", "proficiency": 0.35},
     *        {"name": "页面置换", "proficiency": 0.42}
     *      ]
     *    }
     *    ```
     *
     * 8. 将结果保存到dashboard_cards表的content字段
     *    UPDATE dashboard_cards SET content = ? WHERE id = ?
     *
     * 9. 返回新创建的卡片ID给前端
     *
     * 10. 前端在页面上显示新卡片，使用ECharts等库渲染图表
     *
     * 注意事项：
     * - 建议检查用户的卡片数量限制（例如最多20个）
     * - 刷新间隔建议不低于30秒（30000毫秒），避免服务器压力过大
     * - 查询描述应该限制长度，避免过于复杂的查询
     *
     * @param request 添加卡片的请求对象，包含标题、查询描述、刷新间隔等信息
     * @return Result<Long> 返回新创建的卡片ID
     */
    @Operation(
        summary = "添加自定义看板卡片",
        description = "学生添加自定义数据展示卡片到个人看板，支持设置标题、查询描述、刷新间隔和显示顺序"
    )
    @PostMapping("/card")
    public Result<Long> addDashboardCard(@Valid @RequestBody CreateCardRequest request) {
        try {
            log.info("开始添加自定义看板卡片，标题：{}", request.getTitle());

            // 从Spring Security上下文获取当前登录的学生用户ID
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前登录用户ID: {}", userId);

            // 调用服务层添加卡片
            Long cardId = dashboardService.addDashboardCard(userId, request);

            log.info("成功添加自定义看板卡片，卡片ID：{}", cardId);
            return Result.success("添加卡片成功", cardId);

        } catch (Exception e) {
            log.error("添加自定义看板卡片失败，标题：{}", request.getTitle(), e);
            return Result.error("添加卡片失败：" + e.getMessage());
        }
    }

    /**
     * 删除自定义看板卡片
     * 删除学生创建的指定看板卡片。
     * 安全性说明：
     * - 系统会验证该卡片是否属于当前登录的学生
     * - 只有卡片的创建者才能删除该卡片
     * - 删除操作不可恢复，请谨慎使用
     * 数据库操作：
     * 从dashboard_cards表删除指定ID的记录，删除前需要验证：
     * - 卡片ID是否存在
     * - 卡片的user_id是否与当前用户ID一致
     * @param id 要删除的卡片ID
     * @return Result<Void> 统一响应结果，不包含数据
     */
    @Operation(
        summary = "删除自定义看板卡片",
        description = "删除学生创建的指定看板卡片，只有卡片创建者才能删除"
    )
    @DeleteMapping("/card/{id}")
    public Result<Void> deleteDashboardCard(
            @Parameter(description = "卡片ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            log.info("开始删除看板卡片，卡片ID：{}", id);

            // 从Spring Security上下文获取当前登录的学生用户ID
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前登录用户ID: {}", userId);

            // 调用服务层删除卡片
            dashboardService.deleteDashboardCard(userId, id);

            log.info("成功删除看板卡片，卡片ID：{}", id);
            return Result.success("删除卡片成功", null);

        } catch (Exception e) {
            log.error("删除看板卡片失败，卡片ID：{}", id, e);
            return Result.error("删除卡片失败：" + e.getMessage());
        }
    }

    /**
     * 更新自定义看板卡片
     * 更新学生创建的看板卡片信息，可以修改标题、查询描述、刷新间隔和显示顺序
     * @param id 卡片ID
     * @param request 更新卡片的请求对象
     * @return Result<Void> 统一响应结果
     */
    @Operation(
        summary = "更新自定义看板卡片",
        description = "更新学生创建的看板卡片信息，包括标题、查询描述、刷新间隔和显示顺序"
    )
    @PutMapping("/card/{id}")
    public Result<Void> updateDashboardCard(
            @Parameter(description = "卡片ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CreateCardRequest request) {
        try {
            log.info("开始更新看板卡片，卡片ID：{}，新标题：{}", id, request.getTitle());

            // 从Spring Security上下文获取当前登录的学生用户ID
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前登录用户ID: {}", userId);

            // 调用服务层更新卡片
            dashboardService.updateDashboardCard(userId, id, request);

            log.info("成功更新看板卡片，卡片ID：{}", id);
            return Result.success("更新卡片成功", null);

        } catch (Exception e) {
            log.error("更新看板卡片失败，卡片ID：{}", id, e);
            return Result.error("更新卡片失败：" + e.getMessage());
        }
    }

    /**
     * 获取看板卡片列表
     * 获取当前学生的所有自定义看板卡片，按card_order字段升序排列
     * @return Result 卡片列表
     */
    @Operation(
        summary = "获取看板卡片列表",
        description = "获取当前学生的所有自定义看板卡片，按显示顺序排列"
    )
    @GetMapping("/cards")
    public Result<List<DashboardCardResponse>> getDashboardCards() {
        try {
            log.info("开始获取看板卡片列表");

            // 从Spring Security上下文获取当前登录的学生用户ID
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前登录用户ID: {}", userId);

            // 调用服务层获取卡片列表
            List<DashboardCardResponse> cards = dashboardService.getDashboardCards(userId);

            log.info("成功获取看板卡片列表，共{}个卡片", cards.size());
            return Result.success("获取卡片列表成功", cards);

        } catch (Exception e) {
            log.error("获取看板卡片列表失败", e);
            return Result.error("获取卡片列表失败：" + e.getMessage());
        }
    }

    /**
     * 刷新看板卡片数据
     * 手动触发指定卡片的数据刷新，重新执行查询并更新卡片内容
     * @param id 卡片ID
     * @return Result 刷新后的卡片数据
     */
    @Operation(
        summary = "刷新看板卡片数据",
        description = "手动触发指定卡片的数据刷新，重新执行查询并更新内容"
    )
    @PostMapping("/card/{id}/refresh")
    public Result<DashboardCardResponse> refreshDashboardCard(
            @Parameter(description = "卡片ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            log.info("开始刷新看板卡片数据，卡片ID：{}", id);

            // 从Spring Security上下文获取当前登录的学生用户ID
            Long userId = SecurityUtil.getCurrentUserId();
            log.info("当前登录用户ID: {}", userId);

            // 调用服务层刷新卡片数据
            DashboardCardResponse cardData = dashboardService.refreshDashboardCard(userId, id);

            log.info("成功刷新看板卡片数据，卡片ID：{}", id);
            return Result.success("刷新卡片数据成功", cardData);

        } catch (Exception e) {
            log.error("刷新看板卡片数据失败，卡片ID：{}", id, e);
            return Result.error("刷新卡片数据失败：" + e.getMessage());
        }
    }

    /**
     * 构建示例看板数据（临时方法，用于演示）
     * 在实际开发中，这些数据应该从数据库查询获得。
     * 数据来源说明：
     * 1. 基本统计 - 从students表查询totalQuestions和totalScores字段
     * 2. 问卷统计 - 从questionnaire_submissions表统计已完成和待完成的问卷数量
     * 3. 成绩分析 - 从answers表和questionnaire_submissions表计算平均分、最高分等
     * 4. 学习进度 - 从answers表按knowledge_point字段分组统计各知识点的掌握情况
     * 5. 近期活动 - 从questionnaire_submissions表按submit_time倒序查询最近的提交记录
     * 6. 自定义卡片 - 从dashboard_cards表查询当前用户创建的卡片
     *
     * @return DashboardResponse 示例看板数据
     */
    private DashboardResponse buildSampleDashboardData() {
        // TODO: 实际开发中，从数据库查询真实数据
        // 这里仅返回一个空对象作为占位符
        //
        // 真实实现需要执行以下SQL查询（基于实际数据库表结构）：
        //
        // 1. 查询学生基本信息（students表与users表JOIN）：
        //    SELECT s.student_number, s.major, s.grade, s.class_name,
        //           s.total_questions, s.total_scores, u.nickname
        //    FROM students s
        //    JOIN users u ON s.user_id = u.id
        //    WHERE s.user_id = ?
        //
        // 2. 统计已完成问卷数量（questionnaire_submissions表）：
        //    SELECT COUNT(*) as completed_count
        //    FROM questionnaire_submissions
        //    WHERE student_id = ?
        //
        // 3. 统计待完成问卷数量（questionnaires表）：
        //    SELECT COUNT(*) as pending_count
        //    FROM questionnaires q
        //    LEFT JOIN questionnaire_submissions qs
        //      ON q.id = qs.questionnaire_id AND qs.student_id = ?
        //    WHERE q.status = 'PUBLISHED'
        //      AND q.deadline > NOW()
        //      AND qs.id IS NULL
        //
        // 4. 计算成绩统计（questionnaire_submissions表）：
        //    SELECT AVG(total_score) as avg_score,
        //           MAX(total_score) as max_score,
        //           MIN(total_score) as min_score,
        //           AVG(time_spent) as avg_time_spent
        //    FROM questionnaire_submissions
        //    WHERE student_id = ?
        //
        // 5. 查询成绩趋势（questionnaire_submissions表与questionnaires表JOIN）：
        //    SELECT qs.total_score, qs.submit_time,
        //           q.title, q.total_score as max_score
        //    FROM questionnaire_submissions qs
        //    JOIN questionnaires q ON qs.questionnaire_id = q.id
        //    WHERE qs.student_id = ?
        //    ORDER BY qs.submit_time ASC
        //
        // 6. 查询知识点掌握情况（answers表与questions表JOIN）：
        //    SELECT q.knowledge_point,
        //           COUNT(*) as total,
        //           SUM(CASE WHEN a.is_correct = 1 THEN 1 ELSE 0 END) as correct,
        //           AVG(a.score) as avg_score
        //    FROM answers a
        //    JOIN questions q ON a.question_id = q.id
        //    WHERE a.student_id = ?
        //    GROUP BY q.knowledge_point
        //
        // 7. 查询自定义卡片（dashboard_cards表）：
        //    SELECT id, title, query, content,
        //           refresh_interval, card_order, update_time
        //    FROM dashboard_cards
        //    WHERE user_id = ?
        //    ORDER BY card_order ASC
        //
        // 8. 推荐学习资料（materials表，基于薄弱知识点）：
        //    SELECT id, title, type, file_url, download_count
        //    FROM materials
        //    WHERE JSON_CONTAINS(knowledge_points, '"虚拟内存"')
        //      AND is_public = TRUE
        //    ORDER BY download_count DESC
        //    LIMIT 5

        return new DashboardResponse();
    }
}
