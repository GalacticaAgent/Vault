package com.vault.service.teacher;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vault.dto.response.TeacherDashboardResponse;
import com.vault.entity.mysql.Student;
import com.vault.entity.mysql.Teacher;
import com.vault.entity.mysql.User;
import com.vault.entity.mysql.QuestionnaireSubmission;
import com.vault.entity.mysql.DashboardCard;
import com.vault.dto.request.CreateCardRequest;
import com.vault.mapper.StudentMapper;
import com.vault.mapper.TeacherMapper;
import com.vault.mapper.UserMapper;
import com.vault.mapper.QuestionnaireSubmissionMapper;
import com.vault.mapper.DashboardCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;
    private final QuestionnaireSubmissionMapper questionnaireSubmissionMapper;
    private final DashboardCardMapper dashboardCardMapper;
    private final QueryIntentParser intentParser = new QueryIntentParser();

    private static final long TTL_MS = 10 * 60 * 1000L;
    private static final Map<String, CacheEntry> CACHE = new HashMap<>();

    private static class CacheEntry {
        TeacherDashboardResponse data;
        long ts;
        CacheEntry(TeacherDashboardResponse d, long t) { this.data = d; this.ts = t; }
    }

    public TeacherDashboardResponse getDashboard(Long userId, String className) {
        String key = userId + ":" + (className == null ? "ALL" : className);
        CacheEntry ce = CACHE.get(key);
        long now = System.currentTimeMillis();
        if (ce != null && now - ce.ts < TTL_MS) {
            return ce.data;
        }

        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUserId, userId));
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        LambdaQueryWrapper<Student> stuWrapper = new LambdaQueryWrapper<>();
        if (className != null && !className.isEmpty()) {
            stuWrapper.eq(Student::getClassName, className);
        }
        List<Student> students = studentMapper.selectList(stuWrapper);

        Map<Long, User> userMap = Collections.emptyMap();
        if (!students.isEmpty()) {
            List<Long> userIds = students.stream().map(Student::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(userIds);
                userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
            }
        }

        int studentCount = students.size();
        BigDecimal avgScore = BigDecimal.ZERO;
        if (studentCount > 0) {
            BigDecimal total = students.stream()
                    .map(s -> s.getTotalScores() != null ? s.getTotalScores() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            avgScore = total.divide(BigDecimal.valueOf(studentCount), 2, RoundingMode.HALF_UP);
        }

        int submittedDistinctCount = 0;
        if (studentCount > 0) {
            QueryWrapper<QuestionnaireSubmission> qw = new QueryWrapper<>();
            if (className != null && !className.isEmpty()) {
                List<Long> classStudentIds = students.stream().map(Student::getId).collect(Collectors.toList());
                if (!classStudentIds.isEmpty()) {
                    qw.select("DISTINCT student_id").in("student_id", classStudentIds);
                    List<QuestionnaireSubmission> subs = questionnaireSubmissionMapper.selectList(qw);
                    submittedDistinctCount = subs.size();
                }
            } else {
                qw.select("DISTINCT student_id");
                List<QuestionnaireSubmission> subs = questionnaireSubmissionMapper.selectList(qw);
                submittedDistinctCount = subs.size();
            }
        }
        BigDecimal questionnaireCompletionRate = BigDecimal.ZERO;
        if (studentCount > 0) {
            questionnaireCompletionRate = BigDecimal.valueOf(submittedDistinctCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(studentCount), 2, RoundingMode.HALF_UP);
        }

        int activeStudents = 0;
        if (!userMap.isEmpty()) {
            java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusDays(7);
            activeStudents = (int) userMap.values().stream()
                    .filter(u -> u.getLastLoginTime() != null && u.getLastLoginTime().isAfter(threshold))
                    .count();
        }

        final Map<Long, User> um = userMap;
        List<TeacherDashboardResponse.StudentItem> studentItems = students.stream().map(s -> {
            User u = um.get(s.getUserId());
            String name = u != null ? (u.getNickname() != null ? u.getNickname() : u.getUsername()) : null;
            return TeacherDashboardResponse.StudentItem.builder()
                    .id(s.getId())
                    .username(name)
                    .className(s.getClassName())
                    .totalScores(s.getTotalScores())
                    .build();
        }).collect(Collectors.toList());

        final BigDecimal qcRateFinal = questionnaireCompletionRate;
        final int activeStudentsFinal = activeStudents;
        List<TeacherDashboardResponse.ClassItem> classes = students.stream()
                .collect(Collectors.groupingBy(Student::getClassName))
                .entrySet().stream()
                .map(e -> {
                    String cn = e.getKey();
                    List<Student> list = e.getValue();
                    int count = list.size();
                    BigDecimal avg = BigDecimal.ZERO;
                    if (count > 0) {
                        BigDecimal sum = list.stream()
                                .map(s -> s.getTotalScores() != null ? s.getTotalScores() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                    }
                    return TeacherDashboardResponse.ClassItem.builder()
                            .className(cn)
                            .studentCount(count)
                            .avgScore(avg)
                            .questionnaireCompletionRate(qcRateFinal)
                            .activeStudents(activeStudentsFinal)
                            .build();
                }).collect(Collectors.toList());

        TeacherDashboardResponse.FixedCards fixedCards = TeacherDashboardResponse.FixedCards.builder()
                .avgScore(avgScore)
                .questionnaireCompletionRate(qcRateFinal)
                .activeStudents(activeStudentsFinal)
                .commonIssuesTopN(Collections.emptyList())
                .build();

        TeacherDashboardResponse.TeacherInfo teacherInfo = TeacherDashboardResponse.TeacherInfo.builder()
                .id(teacher.getId())
                .teacherNumber(teacher.getTeacherNumber())
                .department(teacher.getDepartment())
                .title(teacher.getTitle())
                .build();

        TeacherDashboardResponse result = TeacherDashboardResponse.builder()
                .teacher(teacherInfo)
                .classes(classes)
                .fixedCards(fixedCards)
                .students(studentItems)
                .cards(dashboardCardMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.vault.entity.mysql.DashboardCard>()
                        .eq(com.vault.entity.mysql.DashboardCard::getUserId, userId)
                        .orderByAsc(com.vault.entity.mysql.DashboardCard::getCardOrder)
                        .orderByDesc(com.vault.entity.mysql.DashboardCard::getUpdateTime)))
                .build();
        
        CACHE.put(key, new CacheEntry(result, System.currentTimeMillis()));
        return result;
    }

    public Long addCard(Long userId, CreateCardRequest request) {
        DashboardCard card = new DashboardCard();
        card.setUserId(userId);
        card.setTitle(request.getTitle());
        card.setQuery(request.getQuery());
        card.setRefreshInterval(request.getRefreshInterval());
        card.setCardOrder(request.getCardOrder());
        String content = generateContent(userId, request.getQuery());
        card.setContent(content);
        dashboardCardMapper.insert(card);
        return card.getId();
    }

    public java.util.List<DashboardCard> listCards(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DashboardCard> qw =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        qw.eq(DashboardCard::getUserId, userId)
          .orderByAsc(DashboardCard::getCardOrder)
          .orderByDesc(DashboardCard::getUpdateTime);
        return dashboardCardMapper.selectList(qw);
    }

    public boolean deleteCard(Long userId, Long id) {
        DashboardCard card = dashboardCardMapper.selectById(id);
        if (card == null || !Objects.equals(card.getUserId(), userId)) {
            return false;
        }
        return dashboardCardMapper.deleteById(id) > 0;
    }

    public DashboardCard refreshCard(Long userId, Long id) {
        DashboardCard card = dashboardCardMapper.selectById(id);
        if (card == null || !Objects.equals(card.getUserId(), userId)) {
            return null;
        }
        String content = generateContent(userId, card.getQuery());
        card.setContent(content);
        dashboardCardMapper.updateById(card);
        return card;
    }

    private String generateContent(Long userId, String query) {
        QueryIntentParser.Intent intent = intentParser.parse(query);
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<>());
        int studentCount = students.size();
        java.math.BigDecimal avgScore = java.math.BigDecimal.ZERO;
        if (studentCount > 0) {
            java.math.BigDecimal total = students.stream()
                    .map(s -> s.getTotalScores() != null ? s.getTotalScores() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            avgScore = total.divide(java.math.BigDecimal.valueOf(studentCount), 2, java.math.RoundingMode.HALF_UP);
        }
        int submittedDistinctCount = 0;
        if (studentCount > 0) {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<QuestionnaireSubmission> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            qw.select("DISTINCT student_id");
            List<QuestionnaireSubmission> subs = questionnaireSubmissionMapper.selectList(qw);
            submittedDistinctCount = subs.size();
        }
        java.math.BigDecimal questionnaireCompletionRate = java.math.BigDecimal.ZERO;
        if (studentCount > 0) {
            questionnaireCompletionRate = java.math.BigDecimal.valueOf(submittedDistinctCount)
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .divide(java.math.BigDecimal.valueOf(studentCount), 2, java.math.RoundingMode.HALF_UP);
        }
        List<Long> userIds = students.stream().map(Student::getUserId).filter(Objects::nonNull).distinct().collect(java.util.stream.Collectors.toList());
        int activeStudents = 0;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusDays(7);
            activeStudents = (int) users.stream().filter(u -> u.getLastLoginTime() != null && u.getLastLoginTime().isAfter(threshold)).count();
        }
        if (intent.getType() == QueryIntentParser.QueryType.AVG_SCORE) {
            return "平均分: " + avgScore;
        }
        if (intent.getType() == QueryIntentParser.QueryType.QUESTIONNAIRE_RATE) {
            return "问卷完成率: " + questionnaireCompletionRate + "%";
        }
        if (intent.getType() == QueryIntentParser.QueryType.ACTIVE_STUDENTS) {
            return "近7天活跃学生: " + activeStudents;
        }
        return "平均分: " + avgScore + "；问卷完成率: " + questionnaireCompletionRate + "%" + "；近7天活跃学生: " + activeStudents;
    }

    public com.vault.dto.response.TeacherStudentDetailResponse getStudentDetail(Long userId, Long studentId) {
        Student s = studentMapper.selectById(studentId);
        if (s == null) return null;
        User u = s.getUserId() != null ? userMapper.selectById(s.getUserId()) : null;
        int submitted = 0;
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<QuestionnaireSubmission> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("student_id", studentId);
        List<QuestionnaireSubmission> subs = questionnaireSubmissionMapper.selectList(qw);
        submitted = subs.size();
        java.math.BigDecimal rate = submitted > 0 ? java.math.BigDecimal.valueOf(100) : java.math.BigDecimal.ZERO;
        boolean active = false;
        if (u != null && u.getLastLoginTime() != null) {
            active = u.getLastLoginTime().isAfter(java.time.LocalDateTime.now().minusDays(7));
        }
        String name = u != null ? (u.getNickname() != null ? u.getNickname() : u.getUsername()) : null;
        return com.vault.dto.response.TeacherStudentDetailResponse.builder()
                .id(s.getId())
                .username(name)
                .className(s.getClassName())
                .totalScores(s.getTotalScores())
                .totalQuestions(s.getTotalQuestions())
                .questionnaireCompletionRate(rate)
                .activeIn7Days(active)
                .build();
    }

    public java.util.List<com.vault.dto.response.TeacherDashboardResponse.ClassItem> compareClasses(Long userId, java.util.List<String> classNames) {
        if (classNames == null || classNames.isEmpty()) return java.util.Collections.emptyList();
        List<Student> students = studentMapper.selectList(new LambdaQueryWrapper<Student>().in(Student::getClassName, classNames));
        int studentCount = students.size();
        int submittedDistinctCount = 0;
        if (studentCount > 0) {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<QuestionnaireSubmission> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            List<Long> sids = students.stream().map(Student::getId).toList();
            if (!sids.isEmpty()) {
                qw.select("DISTINCT student_id").in("student_id", sids);
                List<QuestionnaireSubmission> subs = questionnaireSubmissionMapper.selectList(qw);
                submittedDistinctCount = subs.size();
            }
        }
        java.math.BigDecimal qcRate = java.math.BigDecimal.ZERO;
        if (studentCount > 0) {
            qcRate = java.math.BigDecimal.valueOf(submittedDistinctCount)
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .divide(java.math.BigDecimal.valueOf(studentCount), 2, java.math.RoundingMode.HALF_UP);
        }
        List<Long> userIds = students.stream().map(Student::getUserId).filter(Objects::nonNull).distinct().collect(java.util.stream.Collectors.toList());
        int activeStudents = 0;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusDays(7);
            activeStudents = (int) users.stream().filter(u -> u.getLastLoginTime() != null && u.getLastLoginTime().isAfter(threshold)).count();
        }
        final java.math.BigDecimal qcRateFinal = qcRate;
        final int activeFinal = activeStudents;
        return students.stream()
                .collect(java.util.stream.Collectors.groupingBy(Student::getClassName))
                .entrySet().stream()
                .map(e -> {
                    String cn = e.getKey();
                    List<Student> list = e.getValue();
                    int count = list.size();
                    java.math.BigDecimal avg = java.math.BigDecimal.ZERO;
                    if (count > 0) {
                        java.math.BigDecimal sum = list.stream()
                                .map(s -> s.getTotalScores() != null ? s.getTotalScores() : java.math.BigDecimal.ZERO)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        avg = sum.divide(java.math.BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
                    }
                    return com.vault.dto.response.TeacherDashboardResponse.ClassItem.builder()
                            .className(cn)
                            .studentCount(count)
                            .avgScore(avg)
                            .questionnaireCompletionRate(qcRateFinal)
                            .activeStudents(activeFinal)
                            .build();
                }).collect(java.util.stream.Collectors.toList());
    }
}
