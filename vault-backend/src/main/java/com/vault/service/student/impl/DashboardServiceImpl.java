package com.vault.service.student.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.dto.request.CreateCardRequest;
import com.vault.dto.response.DashboardCardResponse;
import com.vault.dto.response.DashboardResponse;
import com.vault.entity.mysql.Chat;
import com.vault.entity.mysql.DashboardCard;
import com.vault.entity.mysql.Questionnaire;
import com.vault.entity.mysql.QuestionnaireSubmission;
import com.vault.entity.mysql.Student;
import com.vault.entity.mysql.User;
import com.vault.exception.BusinessException;
import com.vault.mapper.ChatMapper;
import com.vault.mapper.DashboardCardMapper;
import com.vault.mapper.QuestionnaireMapper;
import com.vault.mapper.QuestionnaireSubmissionMapper;
import com.vault.mapper.StudentMapper;
import com.vault.mapper.UserMapper;
import com.vault.service.student.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生看板服务实现
 * <p>
 * 实现学生看板的所有数据查询功能（简化版 - 不包含问卷功能）
 *
 * @author Vault Team
 * @since 2026-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final ChatMapper chatMapper;
    private final DashboardCardMapper dashboardCardMapper;
    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionnaireSubmissionMapper questionnaireSubmissionMapper;

    @Override
    public DashboardResponse getDashboardData(Long userId, String timeRange) {
        log.info("开始获取学生看板数据, userId={}, timeRange={}", userId, timeRange);

        try {
            // 1. 查询学生信息
            Student student = getStudentByUserId(userId);
            if (student == null) {
                log.warn("未找到学生信息, userId={}", userId);
                return buildEmptyDashboard();
            }

            // 2. 构建看板数据（简化版 - 仅包含基本信息、对话和自定义卡片）
            DashboardResponse dashboard = DashboardResponse.builder()
                    .basicStats(buildBasicStats(student, userId, timeRange))
                    .questionnaireStats(buildEmptyQuestionnaireStats())
                    .scoreAnalysis(buildEmptyScoreAnalysis())
                    .rankInfo(buildMockRankInfo())
                    .knowledgePointProgress(Collections.emptyList())
                    .weakKnowledgePoints(buildMockWeakKnowledgePoints())
                    .recommendedMaterials(Collections.emptyList())
                    .recentActivities(buildRecentActivities(userId))
                    .customCards(buildCustomCards(userId))
                    .studyTrends(buildMockStudyTrends(timeRange))
                    .currentLearningProgress(buildMockCurrentLearningProgress())
                    .pendingQuestionnaires(buildPendingQuestionnaires(student.getId()))
                    .build();

            log.info("成功获取学生看板数据, userId={}, studentId={}, timeRange={}", userId, student.getId(), timeRange);
            return dashboard;

        } catch (Exception e) {
            log.error("获取学生看板数据失败, userId={}", userId, e);
            return buildEmptyDashboard();
        }
    }

    /**
     * 根据用户ID查询学生信息
     */
    private Student getStudentByUserId(Long userId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        return studentMapper.selectOne(wrapper);
    }

    /**
     * 构建基本统计信息
     */
    private DashboardResponse.BasicStats buildBasicStats(Student student, Long userId, String timeRange) {
        // 查询用户信息获取昵称
        User user = userMapper.selectById(userId);

        // 构建学生标签（基于成绩和活跃度）
        List<String> tags = new ArrayList<>();
        if (student.getTotalQuestions() != null && student.getTotalQuestions() > 10) {
            tags.add("积极提问");
        }
        if (student.getTotalScores() != null && student.getTotalScores().compareTo(new BigDecimal("80")) >= 0) {
            tags.add("成绩优秀");
        }

        // 根据时间范围计算学习时长
        double studyHours = "month".equals(timeRange) ? 48.5 : 12.5;
        int activeDays = "month".equals(timeRange) ? 22 : 5;

        return DashboardResponse.BasicStats.builder()
                .name(user != null ? user.getNickname() : null)
                .studentNumber(student.getStudentNumber())
                .major(student.getMajor())
                .grade(student.getGrade())
                .className(student.getClassName())
                .currentCourse("操作系统")  // 后续可从课程表获取
                .tags(tags)
                .weeklyStudyHours(studyHours)
                .continuousActiveDays(activeDays)
                .overallRankPercentile("Top 15%")  // 模拟数据，后续可从排名缓存获取
                .totalQuestions(student.getTotalQuestions() != null ? student.getTotalQuestions() : 0)
                .totalScores(student.getTotalScores() != null ? student.getTotalScores() : BigDecimal.ZERO)
                .build();
    }

    /**
     * 构建空的问卷统计信息（问卷功能未实现）
     */
    private DashboardResponse.QuestionnaireStats buildEmptyQuestionnaireStats() {
        return DashboardResponse.QuestionnaireStats.builder()
                .completedCount(0)
                .pendingCount(0)
                .totalCount(0)
                .completionRate(0.0)
                .avgTimeSpent(0)
                .build();
    }

    /**
     * 构建空的成绩分析数据（问卷功能未实现）
     */
    private DashboardResponse.ScoreAnalysis buildEmptyScoreAnalysis() {
        return DashboardResponse.ScoreAnalysis.builder()
                .avgScore(BigDecimal.ZERO)
                .maxScore(BigDecimal.ZERO)
                .minScore(BigDecimal.ZERO)
                .passRate(0.0)
                .scoreTrends(Collections.emptyList())
                .scoreDistribution(DashboardResponse.ScoreDistribution.builder()
                        .range0to59(0)
                        .range60to69(0)
                        .range70to79(0)
                        .range80to89(0)
                        .range90to100(0)
                        .build())
                .build();
    }

    /**
     * 构建模拟排名信息（用于演示）
     */
    private DashboardResponse.RankInfo buildMockRankInfo() {
        return DashboardResponse.RankInfo.builder()
                .classRank(1)
                .totalStudents(45)
                .percentile(95.0)
                .rankChange(2)
                .lastCalculated(LocalDateTime.now().minusDays(1))
                .build();
    }

    /**
     * 构建模拟薄弱知识点（用于演示）
     */
    private List<DashboardResponse.WeakKnowledgePoint> buildMockWeakKnowledgePoints() {
        List<DashboardResponse.WeakKnowledgePoint> weakPoints = new ArrayList<>();
        
        weakPoints.add(DashboardResponse.WeakKnowledgePoint.builder()
                .knowledgeName("并发控制")
                .proficiency(0.45)
                .wrongCount(8)
                .lastAssessed(LocalDateTime.now().minusDays(2))
                .suggestion("建议复习进程同步机制，特别是信号量和管程的使用")
                .build());
                
        weakPoints.add(DashboardResponse.WeakKnowledgePoint.builder()
                .knowledgeName("页面置换算法")
                .proficiency(0.52)
                .wrongCount(6)
                .lastAssessed(LocalDateTime.now().minusDays(3))
                .suggestion("重点理解LRU和Clock算法的实现原理")
                .build());
                
        weakPoints.add(DashboardResponse.WeakKnowledgePoint.builder()
                .knowledgeName("死锁检测")
                .proficiency(0.58)
                .wrongCount(5)
                .lastAssessed(LocalDateTime.now().minusDays(5))
                .suggestion("加强对资源分配图和银行家算法的练习")
                .build());
                
        return weakPoints;
    }

    /**
     * 构建模拟学习投入趋势（用于演示）
     */
    private List<DashboardResponse.StudyTrend> buildMockStudyTrends(String timeRange) {
        List<DashboardResponse.StudyTrend> trends = new ArrayList<>();
        
        if ("month".equals(timeRange)) {
            // 本月数据 - 30天
            String[] days = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
                           "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                           "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
            double[] hours = {1.5, 2.0, 1.8, 3.2, 2.5, 4.0, 3.5, 2.2, 1.9, 3.8,
                            4.5, 3.2, 2.8, 3.6, 4.2, 3.9, 2.5, 3.1, 4.8, 3.5,
                            2.7, 4.1, 3.3, 2.9, 5.2, 4.6, 3.4, 2.8, 4.0, 3.7};
            
            for (int i = 0; i < Math.min(days.length, hours.length); i++) {
                trends.add(DashboardResponse.StudyTrend.builder()
                        .date(LocalDateTime.now().minusDays(29 - i).toLocalDate().toString())
                        .dayOfWeek(days[i])
                        .studyHours(hours[i])
                        .questionCount(i % 5 + 2)
                        .build());
            }
        } else {
            // 本周数据 - 7天
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            double[] hours = {2.5, 4.0, 3.2, 5.5, 3.8, 6.0, 4.5};
            
            for (int i = 0; i < days.length; i++) {
                trends.add(DashboardResponse.StudyTrend.builder()
                        .date(LocalDateTime.now().minusDays(6 - i).toLocalDate().toString())
                        .dayOfWeek(days[i])
                        .studyHours(hours[i])
                        .questionCount(i + 3)
                        .build());
            }
        }
        
        return trends;
    }

    /**
     * 构建模拟当前学习进度（用于演示）
     */
    private DashboardResponse.CurrentLearningProgress buildMockCurrentLearningProgress() {
        return DashboardResponse.CurrentLearningProgress.builder()
                .courseName("操作系统 (Operating System)")
                .chapterName("第 4 章：文件管理系统")
                .sectionName("4.2 文件目录结构")
                .progress(70)
                .build();
    }

    /**
     * 构建近期活动（仅包含聊天对话）
     */
    private List<DashboardResponse.RecentActivity> buildRecentActivities(Long userId) {
        List<DashboardResponse.RecentActivity> activities = new ArrayList<>();

        // 查询最近创建的对话（最近10个）
        LambdaQueryWrapper<Chat> chatWrapper = new LambdaQueryWrapper<>();
        chatWrapper.eq(Chat::getUserId, userId)
                .orderByDesc(Chat::getCreateTime)
                .last("LIMIT 10");

        List<Chat> recentChats = chatMapper.selectList(chatWrapper);
        for (Chat chat : recentChats) {
            activities.add(DashboardResponse.RecentActivity.builder()
                    .activityType("CHAT_CREATED")
                    .description("创建了对话《" + chat.getTitle() + "》")
                    .relatedId(chat.getId())
                    .relatedTitle(chat.getTitle())
                    .activityTime(chat.getCreateTime())
                    .extraData(null)
                    .build());
        }

        return activities;
    }

    /**
     * 构建自定义卡片列表
     */
    private List<DashboardResponse.DashboardCard> buildCustomCards(Long userId) {
        LambdaQueryWrapper<DashboardCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardCard::getUserId, userId)
                .orderByAsc(DashboardCard::getCardOrder);

        List<DashboardCard> cards = dashboardCardMapper.selectList(wrapper);

        return cards.stream()
                .map(card -> DashboardResponse.DashboardCard.builder()
                        .id(card.getId())
                        .title(card.getTitle())
                        .query(card.getQuery())
                        .content(card.getContent())
                        .refreshInterval(card.getRefreshInterval())
                        .cardOrder(card.getCardOrder())
                        .createTime(card.getCreateTime())
                        .updateTime(card.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 构建待完成问卷列表
     * 查询已发布且未提交的问卷
     */
    private List<DashboardResponse.PendingQuestionnaire> buildPendingQuestionnaires(Long studentId) {
        try {
            // 1. 查询所有已发布且未截止的问卷
            LambdaQueryWrapper<Questionnaire> questionnaireWrapper = new LambdaQueryWrapper<>();
            questionnaireWrapper.eq(Questionnaire::getStatus, "PUBLISHED")
                    .gt(Questionnaire::getDeadline, LocalDateTime.now())
                    .orderByAsc(Questionnaire::getDeadline);
            
            List<Questionnaire> allQuestionnaires = questionnaireMapper.selectList(questionnaireWrapper);
            
            // 2. 查询学生已提交的问卷ID
            LambdaQueryWrapper<QuestionnaireSubmission> submissionWrapper = new LambdaQueryWrapper<>();
            submissionWrapper.eq(QuestionnaireSubmission::getStudentId, studentId);
            List<QuestionnaireSubmission> submissions = questionnaireSubmissionMapper.selectList(submissionWrapper);
            List<Long> submittedIds = submissions.stream()
                    .map(QuestionnaireSubmission::getQuestionnaireId)
                    .collect(Collectors.toList());
            
            // 3. 过滤出未提交的问卷
            LocalDateTime now = LocalDateTime.now();
            return allQuestionnaires.stream()
                    .filter(q -> !submittedIds.contains(q.getId()))
                    .map(q -> {
                        // 计算是否紧急（距离截止时间小于24小时）
                        long hoursUntilDeadline = ChronoUnit.HOURS.between(now, q.getDeadline());
                        boolean isUrgent = hoursUntilDeadline <= 24;
                        
                        return DashboardResponse.PendingQuestionnaire.builder()
                                .id(q.getId())
                                .title(q.getTitle())
                                .description(q.getDescription())
                                .deadline(q.getDeadline())
                                .isUrgent(isUrgent)
                                .totalScore(q.getTotalScore())
                                .timeLimit(q.getTimeLimit())
                                .build();
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("查询待完成问卷失败, studentId={}", studentId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 构建空的看板数据（当学生信息不存在时）
     */
    private DashboardResponse buildEmptyDashboard() {
        return DashboardResponse.builder()
                .basicStats(null)
                .questionnaireStats(buildEmptyQuestionnaireStats())
                .scoreAnalysis(buildEmptyScoreAnalysis())
                .rankInfo(null)
                .knowledgePointProgress(Collections.emptyList())
                .weakKnowledgePoints(Collections.emptyList())
                .recommendedMaterials(Collections.emptyList())
                .recentActivities(Collections.emptyList())
                .customCards(Collections.emptyList())
                .build();
    }

    @Override
    public Long addDashboardCard(Long userId, CreateCardRequest request) {
        log.info("开始添加自定义卡片, userId={}, title={}", userId, request.getTitle());

        try {
            // 创建卡片实体
            DashboardCard card = new DashboardCard();
            card.setUserId(userId);
            card.setTitle(request.getTitle());
            card.setQuery(request.getQuery());
            card.setRefreshInterval(request.getRefreshInterval());
            card.setCardOrder(request.getCardOrder());
            
            // 生成模拟内容（真实场景应该调用AI服务解析query并查询数据）
            String mockContent = generateMockCardContent(request.getQuery());
            card.setContent(mockContent);

            // 保存到数据库
            dashboardCardMapper.insert(card);

            log.info("成功添加自定义卡片, cardId={}, userId={}", card.getId(), userId);
            return card.getId();

        } catch (Exception e) {
            log.error("添加自定义卡片失败, userId={}, title={}", userId, request.getTitle(), e);
            throw new BusinessException(500, "添加卡片失败：" + e.getMessage());
        }
    }
    
    /**
     * 生成模拟卡片内容（演示用）
     * 真实场景应该：
     * 1. 调用AI服务理解用户查询
     * 2. 根据AI返回的查询计划执行对应的数据库查询
     * 3. 将查询结果格式化为JSON返回
     */
    private String generateMockCardContent(String query) {
        // 根据查询内容生成不同的模拟数据
        if (query.contains("知识点") || query.contains("薄弱")) {
            return "{\n" +
                   "  \"type\": \"knowledge_points\",\n" +
                   "  \"data\": [\n" +
                   "    {\"name\": \"虚拟内存管理\", \"proficiency\": 0.42, \"wrongCount\": 6},\n" +
                   "    {\"name\": \"进程调度算法\", \"proficiency\": 0.55, \"wrongCount\": 4},\n" +
                   "    {\"name\": \"文件系统实现\", \"proficiency\": 0.61, \"wrongCount\": 3}\n" +
                   "  ]\n" +
                   "}";
        } else if (query.contains("学习时长") || query.contains("学习时间")) {
            return "{\n" +
                   "  \"type\": \"study_time\",\n" +
                   "  \"data\": {\n" +
                   "    \"totalHours\": 28.5,\n" +
                   "    \"weeklyAverage\": 4.1,\n" +
                   "    \"trend\": \"上升\"\n" +
                   "  }\n" +
                   "}";
        } else if (query.contains("成绩") || query.contains("分数")) {
            return "{\n" +
                   "  \"type\": \"scores\",\n" +
                   "  \"data\": {\n" +
                   "    \"average\": 85.6,\n" +
                   "    \"highest\": 95.0,\n" +
                   "    \"lowest\": 72.0,\n" +
                   "    \"rank\": \"Top 15%\"\n" +
                   "  }\n" +
                   "}";
        } else if (query.contains("资料") || query.contains("推荐")) {
            return "{\n" +
                   "  \"type\": \"materials\",\n" +
                   "  \"data\": [\n" +
                   "    {\"title\": \"操作系统原理.pdf\", \"downloadCount\": 245},\n" +
                   "    {\"title\": \"进程管理详解.docx\", \"downloadCount\": 189},\n" +
                   "    {\"title\": \"内存管理习题集.pdf\", \"downloadCount\": 156}\n" +
                   "  ]\n" +
                   "}";
        } else {
            return "{\n" +
                   "  \"type\": \"custom\",\n" +
                   "  \"message\": \"数据已生成\",\n" +
                   "  \"query\": \"" + query + "\",\n" +
                   "  \"timestamp\": \"" + LocalDateTime.now() + "\"\n" +
                   "}";
        }
    }

    @Override
    public List<DashboardCardResponse> getDashboardCards(Long userId) {
        log.info("开始获取卡片列表, userId={}", userId);

        try {
            LambdaQueryWrapper<DashboardCard> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DashboardCard::getUserId, userId)
                    .orderByAsc(DashboardCard::getCardOrder);

            List<DashboardCard> cards = dashboardCardMapper.selectList(wrapper);

            List<DashboardCardResponse> responses = cards.stream()
                    .map(this::convertToCardResponse)
                    .collect(Collectors.toList());

            log.info("成功获取卡片列表, userId={}, count={}", userId, responses.size());
            return responses;

        } catch (Exception e) {
            log.error("获取卡片列表失败, userId={}", userId, e);
            throw new BusinessException(500, "获取卡片列表失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteDashboardCard(Long userId, Long cardId) {
        log.info("开始删除卡片, userId={}, cardId={}", userId, cardId);

        try {
            // 查询卡片是否存在
            DashboardCard card = dashboardCardMapper.selectById(cardId);
            if (card == null) {
                throw new BusinessException(404, "卡片不存在");
            }

            // 验证卡片是否属于当前用户
            if (!card.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权删除该卡片");
            }

            // 删除卡片
            dashboardCardMapper.deleteById(cardId);

            log.info("成功删除卡片, userId={}, cardId={}", userId, cardId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除卡片失败, userId={}, cardId={}", userId, cardId, e);
            throw new BusinessException(500, "删除卡片失败：" + e.getMessage());
        }
    }

    @Override
    public void updateDashboardCard(Long userId, Long cardId, CreateCardRequest request) {
        log.info("开始更新卡片, userId={}, cardId={}, newTitle={}", userId, cardId, request.getTitle());

        try {
            // 查询卡片是否存在
            DashboardCard card = dashboardCardMapper.selectById(cardId);
            if (card == null) {
                throw new BusinessException(404, "卡片不存在");
            }

            // 验证卡片是否属于当前用户
            if (!card.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权更新该卡片");
            }

            // 更新卡片信息
            card.setTitle(request.getTitle());
            card.setQuery(request.getQuery());
            card.setRefreshInterval(request.getRefreshInterval());
            card.setCardOrder(request.getCardOrder());

            // 保存更新
            dashboardCardMapper.updateById(card);

            log.info("成功更新卡片, userId={}, cardId={}", userId, cardId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新卡片失败, userId={}, cardId={}", userId, cardId, e);
            throw new BusinessException(500, "更新卡片失败：" + e.getMessage());
        }
    }

    @Override
    public DashboardCardResponse refreshDashboardCard(Long userId, Long cardId) {
        log.info("开始刷新卡片数据, userId={}, cardId={}", userId, cardId);

        try {
            // 查询卡片是否存在
            DashboardCard card = dashboardCardMapper.selectById(cardId);
            if (card == null) {
                throw new BusinessException(404, "卡片不存在");
            }

            // 验证卡片是否属于当前用户
            if (!card.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权刷新该卡片");
            }

            // 重新生成卡片内容
            String newContent = generateMockCardContent(card.getQuery());
            card.setContent(newContent);
            card.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            dashboardCardMapper.updateById(card);

            log.info("成功刷新卡片数据, userId={}, cardId={}", userId, cardId);
            return convertToCardResponse(card);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("刷新卡片数据失败, userId={}, cardId={}", userId, cardId, e);
            throw new BusinessException(500, "刷新卡片数据失败：" + e.getMessage());
        }
    }

    /**
     * 转换卡片实体为响应对象
     */
    private DashboardCardResponse convertToCardResponse(DashboardCard card) {
        return DashboardCardResponse.builder()
                .id(card.getId())
                .title(card.getTitle())
                .query(card.getQuery())
                .content(card.getContent())
                .refreshInterval(card.getRefreshInterval())
                .cardOrder(card.getCardOrder())
                .createTime(card.getCreateTime())
                .updateTime(card.getUpdateTime())
                .build();
    }
}
