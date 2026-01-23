package com.vault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生看板响应DTO
 *
 * 封装学生个人看板的所有数据，包括固定卡片数据和自定义卡片数据
 * 符合后端技术文档第2节项目结构规范（DashboardResponse.java）
 *
 * 看板结构说明：
 * 1. 固定卡片数据：系统预定义的数据展示，包括基本信息、问卷统计、成绩分析、知识点掌握、近期活动等
 * 2. 自定义卡片数据：学生通过自然语言描述创建的个性化数据展示卡片
 *
 * 数据来源：
 * - MySQL: 学生基本信息、问卷成绩、答题记录
 * - Neo4j: 知识点掌握情况、薄弱知识点、知识图谱数据
 * - Redis: 排名、活跃度等缓存数据
 * - AI服务: 理解自定义查询、生成卡片内容
 *
 * @author Vault Team
 * @since 2026-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    // ==================== 固定卡片数据 ====================
    // 以下数据由系统自动计算和展示，学生无需配置

    /**
     * 基本统计信息（固定卡片）
     * 对应固定卡片："我的学号"、"提问总数"、"问卷分数"等
     * 数据来源：MySQL students表、users表、Redis缓存
     *
     * 数据库表：students
     * 关键字段：student_number, major, grade, class_name, total_questions, total_scores
     */
    private BasicStats basicStats;

    /**
     * 问卷统计信息（固定卡片）
     * 对应固定卡片："已完成问卷"、"待完成问卷"、"完成率"等
     * 数据来源：MySQL questionnaire_submissions表、questionnaires表
     *
     * 数据库表：questionnaire_submissions, questionnaires
     * 关键字段：questionnaire_id, student_id, total_score, time_spent, submit_time
     */
    private QuestionnaireStats questionnaireStats;

    /**
     * 成绩分析数据（固定卡片）
     * 对应固定卡片："平均分"、"成绩趋势"、"分数分布"等
     * 数据来源：MySQL questionnaire_submissions表、answers表
     *
     * 数据库表：questionnaire_submissions, answers
     * 关键字段：total_score, is_correct, score, submit_time
     */
    private ScoreAnalysis scoreAnalysis;

    /**
     * 班级排名信息（固定卡片）
     * 对应固定卡片："班级排名"
     * 数据来源：Redis缓存（每天计算一次）
     */
    private RankInfo rankInfo;

    /**
     * 知识点掌握进度（固定卡片）
     * 对应固定卡片："知识点掌握情况"
     * 数据来源：Neo4j知识图谱 - 学生与知识点的MASTER关系
     */
    private List<KnowledgePointProgress> knowledgePointProgress;

    /**
     * 薄弱知识点列表（固定卡片）
     * 对应固定卡片："薄弱知识点"
     * 显示掌握最不好的几个知识点，帮助学生快速定位需要加强的方向
     * 数据来源：Neo4j知识图谱 - 学生与知识点的WEAK_AT关系
     */
    private List<WeakKnowledgePoint> weakKnowledgePoints;

    /**
     * 推荐学习资料（固定卡片）
     * 对应固定卡片："推荐资料"
     * 基于学生的薄弱知识点推荐相关学习资料
     * 数据来源：Neo4j知识图谱、MySQL materials表
     *
     * 数据库表：materials
     * 关键字段：title, type, file_url, knowledge_points (JSON), download_count
     *
     * 推荐逻辑：
     * 1. 从Neo4j知识图谱查询学生的薄弱知识点
     * 2. 查询materials表中knowledge_points字段（JSON数组）包含这些知识点的资料
     * 3. 按照download_count和相关度排序
     * 4. 返回Top 5推荐资料
     */
    private List<RecommendedMaterial> recommendedMaterials;

    /**
     * 近期活动列表（固定卡片）
     * 对应固定卡片："最近提交"、"待完成任务"
     * 数据来源：MySQL多个表（questionnaire_submissions、materials、chats等）
     */
    private List<RecentActivity> recentActivities;

    // ==================== 自定义卡片数据 ====================
    // 以下数据由学生通过自然语言描述创建，系统使用AI理解并生成

    /**
     * 自定义卡片列表
     *
     * 学生可以通过自然语言描述创建个性化的数据展示卡片
     *
     * 卡片示例：
     * - "我最薄弱的 5 个知识点"
     * - "我最近一周的学习时长"
     * - "我在进程管理方面的掌握程度"
     * - "推荐给我的学习资料"
     *
     * 创建流程：
     * 1. 学生输入查询描述（自然语言）
     * 2. 后端使用AI（LLM）分析查询描述，理解学生想看什么数据
     * 3. AI从知识图谱或数据库中查询相关数据
     * 4. 将查询结果保存到卡片的content字段（JSON格式）
     * 5. 根据刷新间隔自动或手动更新卡片内容
     *
     * 数据来源：dashboard_cards表
     */
    private List<DashboardCard> customCards;

    /**
     * 学习投入趋势数据
     * 用于显示学生每周或每月的学习时长变化
     */
    private List<StudyTrend> studyTrends;

    /**
     * 当前学习进度
     * 显示学生当前正在学习的课程和章节进度
     */
    private CurrentLearningProgress currentLearningProgress;

    /**
     * 基本统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicStats {
        /**
         * 学生姓名
         */
        private String name;

        /**
         * 学号
         */
        private String studentNumber;

        /**
         * 专业
         */
        private String major;

        /**
         * 年级
         */
        private String grade;

        /**
         * 班级
         */
        private String className;

        /**
         * 所在课程/当前学习课程
         */
        private String currentCourse;

        /**
         * 学生标签列表
         * 例如：["理论扎实", "代码规范"]
         */
        private List<String> tags;

        /**
         * 本周学习时间（小时）
         * 统计最近7天的学习时长
         */
        private Double weeklyStudyHours;

        /**
         * 连续活跃天数
         * 统计连续登录或学习的天数
         */
        private Integer continuousActiveDays;

        /**
         * 综合排名（百分比形式，如 "Top 15%"）
         * 基于成绩、学习时长等综合计算
         */
        private String overallRankPercentile;

        /**
         * 总提问次数
         * 从students表的total_questions字段获取
         */
        private Integer totalQuestions;

        /**
         * 累计获得分数
         * 从students表的total_scores字段获取
         */
        private BigDecimal totalScores;
    }

    /**
     * 问卷统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionnaireStats {
        /**
         * 已完成问卷数量
         * 从questionnaire_submissions表统计student_id匹配的记录数
         */
        private Integer completedCount;

        /**
         * 待完成问卷数量
         * 统计status='PUBLISHED'且deadline未过期，但该学生未提交的问卷数
         */
        private Integer pendingCount;

        /**
         * 总问卷数量
         * 已完成 + 待完成
         */
        private Integer totalCount;

        /**
         * 完成率（百分比）
         * 计算公式：(completedCount / totalCount) * 100
         */
        private Double completionRate;

        /**
         * 平均用时（秒）
         * 从questionnaire_submissions表的time_spent字段计算平均值
         */
        private Integer avgTimeSpent;
    }

    /**
     * 成绩分析数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreAnalysis {
        /**
         * 平均分
         * 从questionnaire_submissions表的total_score字段计算平均值
         */
        private BigDecimal avgScore;

        /**
         * 最高分
         * 从questionnaire_submissions表的total_score字段取最大值
         */
        private BigDecimal maxScore;

        /**
         * 最低分
         * 从questionnaire_submissions表的total_score字段取最小值
         */
        private BigDecimal minScore;

        /**
         * 及格率（百分比）
         * 统计total_score >= pass_score的问卷提交占比
         */
        private Double passRate;

        /**
         * 成绩趋势数据
         * 按时间顺序排列的历史成绩数据，用于绘制趋势图
         */
        private List<ScoreTrend> scoreTrends;

        /**
         * 分数分布
         * 各分数段的题目数量统计，用于绘制分布图
         */
        private ScoreDistribution scoreDistribution;
    }

    /**
     * 成绩趋势数据点
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreTrend {
        /**
         * 问卷标题
         */
        private String questionnaireTitle;

        /**
         * 得分
         */
        private BigDecimal score;

        /**
         * 总分
         */
        private BigDecimal totalScore;

        /**
         * 提交时间
         */
        private LocalDateTime submitTime;
    }

    /**
     * 分数分布数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDistribution {
        /**
         * 0-59分题目数量
         */
        private Integer range0to59;

        /**
         * 60-69分题目数量
         */
        private Integer range60to69;

        /**
         * 70-79分题目数量
         */
        private Integer range70to79;

        /**
         * 80-89分题目数量
         */
        private Integer range80to89;

        /**
         * 90-100分题目数量
         */
        private Integer range90to100;
    }

    /**
     * 知识点掌握进度
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgePointProgress {
        /**
         * 知识点名称
         * 从questions表的knowledge_point字段获取
         */
        private String knowledgePoint;

        /**
         * 该知识点的总题目数
         */
        private Integer totalQuestions;

        /**
         * 答对的题目数
         * 从answers表统计is_correct=true的记录数
         */
        private Integer correctQuestions;

        /**
         * 正确率（百分比）
         * 计算公式：(correctQuestions / totalQuestions) * 100
         */
        private Double correctRate;

        /**
         * 平均得分率（百分比）
         * 该知识点下所有题目的平均得分占总分的比例
         */
        private Double avgScoreRate;

        /**
         * 掌握程度
         * 根据正确率判断：
         * - EXCELLENT: 90%以上
         * - GOOD: 75%-90%
         * - FAIR: 60%-75%
         * - POOR: 60%以下
         */
        private String masteryLevel;
    }

    /**
     * 近期活动
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        /**
         * 活动类型
         * 例如：QUESTIONNAIRE_SUBMITTED, MATERIAL_DOWNLOADED, CHAT_CREATED等
         */
        private String activityType;

        /**
         * 活动描述
         * 例如："提交了问卷《Java基础测试》"
         */
        private String description;

        /**
         * 关联对象ID
         * 例如：问卷ID、资料ID、对话ID等
         */
        private Long relatedId;

        /**
         * 关联对象标题
         */
        private String relatedTitle;

        /**
         * 活动时间
         */
        private LocalDateTime activityTime;

        /**
         * 额外数据（JSON格式）
         * 存储活动相关的其他信息，如分数、用时等
         */
        private String extraData;
    }

    /**
     * 看板自定义卡片
     *
     * 学生通过POST /api/student/dashboard/card创建的个性化卡片
     * 系统使用AI理解学生的自然语言查询，从知识图谱或数据库中查询数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardCard {
        /**
         * 卡片ID
         * 来自dashboard_cards表的主键
         */
        private Long id;

        /**
         * 卡片标题
         * 学生输入的卡片标题，简明扼要地描述卡片内容
         * 例如："我最薄弱的5个知识点"
         */
        private String title;

        /**
         * 查询描述（自然语言）
         *
         * 学生用自然语言描述想要查看的数据
         * AI会分析这个描述，理解学生的意图，然后生成相应的查询
         *
         * 示例：
         * - "列出我最薄弱的 5 个知识点"
         * - "我最近一周的学习时长"
         * - "我在进程管理方面的掌握程度"
         * - "推荐给我关于内存管理的学习资料"
         */
        private String query;

        /**
         * 卡片内容（JSON格式）
         *
         * AI根据查询描述生成的实际数据，以JSON格式存储
         * 前端解析这个JSON并渲染成图表或列表
         *
         * 示例JSON：
         * {
         *   "type": "weak_points",
         *   "data": [
         *     {"name": "虚拟内存", "proficiency": 0.35},
         *     {"name": "页面置换", "proficiency": 0.42}
         *   ]
         * }
         */
        private String content;

        /**
         * 刷新间隔（毫秒）
         *
         * 0表示不自动刷新，只在用户手动刷新或页面加载时更新
         * 大于0表示按照指定间隔自动刷新卡片数据
         *
         * 刷新时会重新执行AI查询，获取最新数据
         */
        private Integer refreshInterval;

        /**
         * 卡片顺序
         *
         * 控制卡片在看板中的显示位置
         * 数值越小越靠前，可以为负数
         */
        private Integer cardOrder;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        /**
         * 最后更新时间
         *
         * 记录卡片内容最后一次更新的时间
         * 每次刷新卡片数据时会更新这个字段
         */
        private LocalDateTime updateTime;
    }

    /**
     * 班级排名信息（固定卡片数据）
     *
     * 显示学生在班级中的排名情况
     * 数据来源：Redis缓存（每天凌晨2点计算一次）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankInfo {
        /**
         * 班级内排名
         * 1表示第1名
         */
        private Integer classRank;

        /**
         * 班级总人数
         */
        private Integer totalStudents;

        /**
         * 百分位
         * 表示超过了百分之多少的同学
         * 例如：75表示超过了75%的同学
         */
        private Double percentile;

        /**
         * 排名趋势
         * 与上次相比的排名变化
         * 正数表示排名上升，负数表示排名下降
         */
        private Integer rankChange;

        /**
         * 上次排名计算时间
         */
        private LocalDateTime lastCalculated;
    }

    /**
     * 薄弱知识点（固定卡片数据）
     *
     * 显示学生掌握最薄弱的知识点
     * 数据来源：Neo4j知识图谱
     *
     * 查询方式：
     * MATCH (s:Student {studentId: ?})-[m:MASTER]->(kp:KnowledgePoint)
     * WHERE m.proficiency < 0.6
     * RETURN kp.name, m.proficiency
     * ORDER BY m.proficiency ASC
     * LIMIT 5
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeakKnowledgePoint {
        /**
         * 知识点名称
         * 例如："虚拟内存"、"进程调度"
         */
        private String knowledgeName;

        /**
         * 掌握程度
         * 0.0-1.0之间，值越小表示越薄弱
         */
        private Double proficiency;

        /**
         * 相关题目错误次数
         * 该知识点相关题目的答错次数
         */
        private Integer wrongCount;

        /**
         * 相关题目错误次数（备用字段，与wrongCount相同）
         * 该知识点相关题目的答错次数
         */
        private Integer errorCount;

        /**
         * 最后评估时间
         * 最近一次对该知识点进行评估的时间
         */
        private LocalDateTime lastAssessed;

        /**
         * 建议措施
         * AI生成的学习建议
         * 例如："建议复习《操作系统原理》第5章"
         */
        private String suggestion;
    }

    /**
     * 推荐学习资料（固定卡片数据）
     *
     * 基于学生的薄弱知识点推荐相关学习资料
     * 数据来源：Neo4j知识图谱、MySQL materials表
     *
     * 数据库表：materials
     * 关键字段：id, title, type, file_url, knowledge_points (JSON), download_count
     *
     * 推荐逻辑：
     * 1. 从Neo4j知识图谱查询学生的薄弱知识点
     * 2. 从materials表查询knowledge_points字段包含这些知识点的资料
     *    示例SQL: SELECT * FROM materials WHERE JSON_CONTAINS(knowledge_points, '"虚拟内存"')
     * 3. 按照download_count降序排序
     * 4. 返回Top 5推荐资料
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedMaterial {
        /**
         * 资料ID
         * 对应materials表的id字段
         */
        private Long materialId;

        /**
         * 资料标题
         * 对应materials表的title字段
         * 例如："《操作系统原理》第5章 - 内存管理"
         */
        private String title;

        /**
         * 资料类型
         * 对应materials表的type字段（ENUM类型）
         * TEXTBOOK: 教材
         * LECTURE: 讲义
         * VIDEO: 视频
         * EXAM: 试卷
         * OTHER: 其他
         */
        private String type;

        /**
         * 关联的知识点
         * 从materials表的knowledge_points字段（JSON数组）中提取
         * 说明这个资料对哪个知识点有帮助
         */
        private String relatedKnowledgePoint;

        /**
         * 推荐理由
         * 由后端AI生成的推荐理由
         * 例如："您在虚拟内存方面较为薄弱，这份资料可以帮助您系统学习"
         */
        private String reason;

        /**
         * 资料URL
         * 对应materials表的file_url字段
         * 可以直接下载或查看的链接
         */
        private String fileUrl;

        /**
         * 下载次数
         * 对应materials表的download_count字段
         * 其他学生的下载次数，作为资料质量的参考
         */
        private Integer downloadCount;
    }

    /**
     * 学习投入趋势数据点
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyTrend {
        /**
         * 日期
         */
        private String date;

        /**
         * 星期几
         */
        private String dayOfWeek;

        /**
         * 学习时长（小时）
         */
        private Double studyHours;

        /**
         * 提问数量
         */
        private Integer questionCount;
    }

    /**
     * 当前学习进度
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentLearningProgress {
        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 章节名称
         */
        private String chapterName;

        /**
         * 小节名称
         */
        private String sectionName;

        /**
         * 进度百分比
         */
        private Integer progress;
    }
}
