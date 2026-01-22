package com.vault.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建看板卡片请求DTO
 *
 * 用于接收前端添加或更新自定义看板卡片的请求数据
 * 符合后端技术文档第2节项目结构规范
 *
 * 数据库映射：dashboard_cards表
 * - title -> dashboard_cards.title (VARCHAR(100))
 * - query -> dashboard_cards.query (TEXT)
 * - refreshInterval -> dashboard_cards.refresh_interval (INT)
 * - cardOrder -> dashboard_cards.card_order (INT)
 *
 * @author Vault Team
 * @since 2026-01-21
 */
@Data
public class CreateCardRequest {

    /**
     * 卡片标题
     *
     * 显示在卡片顶部的标题文本，用于标识卡片的内容主题
     *
     * 数据库映射：dashboard_cards.title (VARCHAR(100))
     *
     * 要求：
     * - 不能为空
     * - 长度在1-100个字符之间
     *
     * 示例：
     * - "我的Java课程成绩"
     * - "本周学习进度"
     * - "待完成任务列表"
     */
    @NotBlank(message = "卡片标题不能为空")
    @Size(min = 1, max = 100, message = "卡片标题长度必须在1-100个字符之间")
    private String title;

    /**
     * 查询描述
     *
     * 用自然语言描述需要展示的数据内容，系统将根据该描述生成相应的数据查询。
     *
     * 数据库映射：dashboard_cards.query (TEXT)
     *
     * 要求：
     * - 不能为空
     * - 长度在1-500个字符之间
     *
     * AI处理流程：
     * 1. 后端收到查询描述后，发送给AI（LLM）分析
     * 2. AI理解学生的意图，判断需要查询什么数据
     * 3. AI生成对应的数据库查询语句（SQL、Cypher等）
     * 4. 执行查询，获取数据
     * 5. AI将数据格式化成JSON，存储到卡片的content字段
     *
     * 示例（参考功能详细说明文档 2.2节）：
     * - "我最薄弱的 5 个知识点"
     *   AI会从Neo4j知识图谱查询proficiency < 0.6的知识点
     *
     * - "我最近一周的学习时长"
     *   AI会从MySQL统计最近7天的在线时长或学习活动时间
     *
     * - "我在进程管理方面的掌握程度"
     *   AI会从Neo4j查询"进程管理"相关知识点的掌握情况
     *
     * - "推荐给我的学习资料"
     *   AI会根据薄弱知识点，从Neo4j查询相关的学习资料
     *
     * - "显示我最近7天的问卷提交记录"
     *   AI会从MySQL questionnaire_submissions表查询最近7天的提交记录
     *
     * - "统计我在数据结构课程中各个知识点的掌握情况"
     *   AI会从Neo4j查询"数据结构"相关知识点的MASTER关系
     *
     * - "展示我的成绩排名和班级平均分对比"
     *   AI会从Redis获取排名数据，从MySQL计算班级平均分
     *
     * - "列出所有未完成且即将到期的问卷"
     *   AI会从MySQL questionnaires表查询deadline < 7天的未提交问卷
     */
    @NotBlank(message = "查询描述不能为空")
    @Size(min = 1, max = 500, message = "查询描述长度必须在1-500个字符之间")
    private String query;

    /**
     * 刷新间隔（毫秒）
     *
     * 卡片数据的自动刷新时间间隔，单位为毫秒。
     *
     * 数据库映射：dashboard_cards.refresh_interval (INT, 默认0)
     *
     * - 值为0表示不自动刷新，只在用户手动刷新或页面加载时更新
     * - 值大于0表示每隔指定时间自动刷新一次卡片数据
     *
     * 建议值：
     * - 0: 不自动刷新（默认）
     * - 60000: 每分钟刷新一次
     * - 300000: 每5分钟刷新一次
     * - 600000: 每10分钟刷新一次
     *
     * 注意：
     * - 过于频繁的刷新会增加服务器负载
     * - 建议最小间隔不低于30秒（30000毫秒）
     * - 对于实时性要求不高的数据，建议使用较长的刷新间隔或不自动刷新
     */
    @Min(value = 0, message = "刷新间隔不能为负数")
    private Integer refreshInterval = 0;

    /**
     * 卡片显示顺序
     *
     * 控制卡片在看板中的显示位置，数值越小越靠前。
     *
     * 数据库映射：dashboard_cards.card_order (INT, 默认0)
     *
     * - 默认值为0
     * - 相同顺序值的卡片按创建时间排序
     * - 可以使用负数来确保卡片显示在最前面
     *
     * 示例：
     * - -1: 置顶显示
     * - 0: 默认位置
     * - 1, 2, 3...: 依次排列
     *
     * 用途：
     * - 让重要的卡片显示在顶部
     * - 对卡片进行分组排列
     * - 实现卡片的拖拽排序功能
     */
    private Integer cardOrder = 0;
}
