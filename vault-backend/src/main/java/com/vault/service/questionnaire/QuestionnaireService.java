package com.vault.service.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.dto.request.CreateQuestionnaireRequest;
import com.vault.dto.request.UpdateQuestionnaireRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.dto.response.QuestionnaireListResponse;
import com.vault.entity.mysql.*;
import com.vault.exception.BusinessException;
import com.vault.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问卷服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionMapper questionMapper;
    private final UserMapper userMapper;
    private final QuestionnaireSubmissionMapper submissionMapper;

    /**
     * 创建问卷
     */
    @Transactional(rollbackFor = Exception.class)
    public QuestionnaireDetailResponse createQuestionnaire(
            Long teacherId, CreateQuestionnaireRequest request) {
        // 1. 验证教师身份
        User teacher = validateTeacher(teacherId);

        // 2. 创建问卷主体
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitle(request.getTitle());
        questionnaire.setDescription(request.getDescription());
        questionnaire.setCreatorId(teacherId);
        questionnaire.setTargetStudents(request.getTargetStudents());
        questionnaire.setTimeLimit(request.getTimeLimit());
        questionnaire.setPassScore(request.getPassScore());
        questionnaire.setStartTime(request.getStartTime());
        questionnaire.setDeadline(request.getDeadline());
        questionnaire.setStatus("DRAFT");

        // 3. 计算总分
        BigDecimal totalScore = request.getQuestions().stream()
            .map(CreateQuestionnaireRequest.QuestionDTO::getScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        questionnaire.setTotalScore(totalScore);

        questionnaireMapper.insert(questionnaire);
        log.info("创建问卷成功，ID: {}", questionnaire.getId());

        // 4. 批量插入题目
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < request.getQuestions().size(); i++) {
            CreateQuestionnaireRequest.QuestionDTO dto = request.getQuestions().get(i);
            Question question = new Question();
            question.setQuestionnaireId(questionnaire.getId());
            question.setQuestionOrder(i + 1);
            question.setType(dto.getType());
            question.setContent(dto.getContent());
            question.setOptions(dto.getOptions());
            question.setCorrectAnswer(dto.getCorrectAnswer());
            question.setKnowledgePoint(dto.getKnowledgePoint());
            question.setDifficulty(dto.getDifficulty());
            question.setScore(dto.getScore());
            question.setExplanation(dto.getExplanation());
            questions.add(question);
        }

        questions.forEach(questionMapper::insert);
        log.info("批量创建题目成功，共 {} 道", questions.size());

        // 5. 返回详情
        return getQuestionnaireDetail(questionnaire.getId(), teacherId);
    }

    /**
     * 获取问卷详情
     */
    public QuestionnaireDetailResponse getQuestionnaireDetail(Long id, Long userId) {
        Questionnaire questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw new BusinessException(404, "问卷不存在");
        }

        // 查询创建者信息
        User creator = userMapper.selectById(questionnaire.getCreatorId());
        String creatorName = creator != null ? creator.getNickname() : "未知用户";

        // 查询题目列表
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getQuestionnaireId, id)
               .orderByAsc(Question::getQuestionOrder);
        List<Question> questions = questionMapper.selectList(wrapper);

        // 构建响应
        return QuestionnaireDetailResponse.builder()
            .id(questionnaire.getId())
            .title(questionnaire.getTitle())
            .description(questionnaire.getDescription())
            .creatorId(questionnaire.getCreatorId())
            .creatorName(creatorName)
            .timeLimit(questionnaire.getTimeLimit())
            .totalScore(questionnaire.getTotalScore())
            .passScore(questionnaire.getPassScore())
            .startTime(questionnaire.getStartTime())
            .deadline(questionnaire.getDeadline())
            .status(questionnaire.getStatus())
            .targetStudents(questionnaire.getTargetStudents())
            .targetStudentCount(questionnaire.getTargetStudents() != null ?
                questionnaire.getTargetStudents().size() : 0)
            .createTime(questionnaire.getCreateTime())
            .updateTime(questionnaire.getUpdateTime())
            .questions(buildQuestionVOList(questions))
            .build();
    }

    /**
     * 获取问卷列表（教师）
     */
    public List<QuestionnaireListResponse> getQuestionnaireList(Long teacherId, String status) {
        LambdaQueryWrapper<Questionnaire> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Questionnaire::getCreatorId, teacherId);

        if (status != null && !status.isBlank()) {
            wrapper.eq(Questionnaire::getStatus, status);
        }

        wrapper.orderByDesc(Questionnaire::getCreateTime);
        List<Questionnaire> questionnaires = questionnaireMapper.selectList(wrapper);

        return questionnaires.stream()
            .map(this::buildQuestionnaireListResponse)
            .collect(Collectors.toList());
    }

    /**
     * 更新问卷
     */
    @Transactional(rollbackFor = Exception.class)
    public QuestionnaireDetailResponse updateQuestionnaire(
            Long teacherId, UpdateQuestionnaireRequest request) {
        Questionnaire questionnaire = questionnaireMapper.selectById(request.getId());
        validateOwnership(questionnaire, teacherId);

        // 已发布的问卷不能修改
        if ("PUBLISHED".equals(questionnaire.getStatus())) {
            throw new BusinessException(400, "已发布的问卷不能修改");
        }

        // 更新字段
        if (request.getTitle() != null) questionnaire.setTitle(request.getTitle());
        if (request.getDescription() != null) questionnaire.setDescription(request.getDescription());
        if (request.getTimeLimit() != null) questionnaire.setTimeLimit(request.getTimeLimit());
        if (request.getPassScore() != null) questionnaire.setPassScore(request.getPassScore());
        if (request.getStartTime() != null) questionnaire.setStartTime(request.getStartTime());
        if (request.getDeadline() != null) questionnaire.setDeadline(request.getDeadline());
        if (request.getTargetStudents() != null) questionnaire.setTargetStudents(request.getTargetStudents());

        questionnaireMapper.updateById(questionnaire);
        log.info("更新问卷成功，ID: {}", questionnaire.getId());

        return getQuestionnaireDetail(questionnaire.getId(), teacherId);
    }

    /**
     * 发布问卷
     */
    @Transactional(rollbackFor = Exception.class)
    public void publishQuestionnaire(Long id, Long teacherId) {
        Questionnaire questionnaire = questionnaireMapper.selectById(id);
        validateOwnership(questionnaire, teacherId);

        if (!"DRAFT".equals(questionnaire.getStatus())) {
            throw new BusinessException(400, "只能发布草稿状态的问卷");
        }

        questionnaire.setStatus("PUBLISHED");
        questionnaireMapper.updateById(questionnaire);
        log.info("发布问卷成功，ID: {}", id);
    }

    /**
     * 关闭问卷
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeQuestionnaire(Long id, Long teacherId) {
        Questionnaire questionnaire = questionnaireMapper.selectById(id);
        validateOwnership(questionnaire, teacherId);

        questionnaire.setStatus("CLOSED");
        questionnaireMapper.updateById(questionnaire);
        log.info("关闭问卷成功，ID: {}", id);
    }

    /**
     * 删除问卷
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestionnaire(Long id, Long teacherId) {
        Questionnaire questionnaire = questionnaireMapper.selectById(id);
        validateOwnership(questionnaire, teacherId);

        // 已发布的问卷不能删除
        if ("PUBLISHED".equals(questionnaire.getStatus())) {
            throw new BusinessException(400, "已发布的问卷不能删除，请先关闭");
        }

        // 删除问卷（级联删除题目和答案）
        questionnaireMapper.deleteById(id);
        log.info("删除问卷成功，ID: {}", id);
    }

    // ========== 私有方法 ==========

    private User validateTeacher(Long teacherId) {
        User user = userMapper.selectById(teacherId);
        if (user == null || !"TEACHER".equals(user.getRole())) {
            throw new BusinessException(403, "仅教师可创建问卷");
        }
        return user;
    }

    private void validateOwnership(Questionnaire questionnaire, Long teacherId) {
        if (questionnaire == null) {
            throw new BusinessException(404, "问卷不存在");
        }
        if (!questionnaire.getCreatorId().equals(teacherId)) {
            throw new BusinessException(403, "无权限操作此问卷");
        }
    }

    private List<QuestionnaireDetailResponse.QuestionVO> buildQuestionVOList(List<Question> questions) {
        return questions.stream()
            .map(q -> QuestionnaireDetailResponse.QuestionVO.builder()
                .id(q.getId())
                .questionOrder(q.getQuestionOrder())
                .type(q.getType())
                .content(q.getContent())
                .options(q.getOptions())
                .correctAnswer(q.getCorrectAnswer())
                .knowledgePoint(q.getKnowledgePoint())
                .difficulty(q.getDifficulty())
                .score(q.getScore())
                .explanation(q.getExplanation())
                .build())
            .collect(Collectors.toList());
    }

    private QuestionnaireListResponse buildQuestionnaireListResponse(Questionnaire q) {
        // 统计题目数量
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(Question::getQuestionnaireId, q.getId());
        long questionCount = questionMapper.selectCount(questionWrapper);

        // 统计提交人数
        LambdaQueryWrapper<QuestionnaireSubmission> submissionWrapper = new LambdaQueryWrapper<>();
        submissionWrapper.eq(QuestionnaireSubmission::getQuestionnaireId, q.getId());
        long submittedCount = submissionMapper.selectCount(submissionWrapper);

        return QuestionnaireListResponse.builder()
            .id(q.getId())
            .title(q.getTitle())
            .description(q.getDescription())
            .status(q.getStatus())
            .questionCount((int) questionCount)
            .totalScore(q.getTotalScore())
            .deadline(q.getDeadline())
            .submittedCount((int) submittedCount)
            .targetStudentCount(q.getTargetStudents() != null ? q.getTargetStudents().size() : 0)
            .createTime(q.getCreateTime())
            .build();
    }
}
