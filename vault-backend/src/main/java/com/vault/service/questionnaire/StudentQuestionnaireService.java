package com.vault.service.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.dto.request.SubmitAnswersRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.dto.response.QuestionnaireListResponse;
import com.vault.dto.response.QuestionnaireResultResponse;
import com.vault.entity.mysql.*;
import com.vault.exception.BusinessException;
import com.vault.mapper.*;
import com.vault.service.questionnaire.AnswerGradingService.GradingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生问卷服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentQuestionnaireService {

    private final QuestionnaireMapper questionnaireMapper;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final QuestionnaireSubmissionMapper submissionMapper;
    private final StudentMapper studentMapper;
    private final AnswerGradingService gradingService;
    private final ObjectMapper objectMapper;

    /**
     * 获取问卷列表
     */
    public List<QuestionnaireListResponse> getQuestionnaireList(Long userId, String status) {
        // 获取学生信息
        Student student = studentMapper.selectOne(
            new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId)
        );
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }

        // 查询已发布的问卷
        List<Questionnaire> questionnaires = questionnaireMapper.selectList(
            new LambdaQueryWrapper<Questionnaire>()
                .eq(Questionnaire::getStatus, "PUBLISHED")
                .orderByDesc(Questionnaire::getCreateTime)
        );

        // 查询学生的提交记录
        List<QuestionnaireSubmission> submissions = submissionMapper.selectList(
            new LambdaQueryWrapper<QuestionnaireSubmission>()
                .eq(QuestionnaireSubmission::getStudentId, student.getId())
        );
        Map<Long, QuestionnaireSubmission> submissionMap = submissions.stream()
            .collect(Collectors.toMap(QuestionnaireSubmission::getQuestionnaireId, s -> s));

        // 构建响应
        List<QuestionnaireListResponse> result = new ArrayList<>();
        for (Questionnaire q : questionnaires) {
            QuestionnaireSubmission submission = submissionMap.get(q.getId());
            boolean isCompleted = submission != null;

            // 根据状态过滤
            if ("pending".equals(status) && isCompleted) continue;
            if ("completed".equals(status) && !isCompleted) continue;

            QuestionnaireListResponse response = new QuestionnaireListResponse();
            response.setId(q.getId());
            response.setTitle(q.getTitle());
            response.setDescription(q.getDescription());
            response.setTimeLimit(q.getTimeLimit());
            response.setTotalScore(q.getTotalScore());
            response.setDeadline(q.getDeadline());
            response.setStatus(q.getStatus());
            response.setIsCompleted(isCompleted);

            if (isCompleted) {
                response.setMyScore(submission.getTotalScore());
                response.setSubmitTime(submission.getSubmitTime());
            }

            result.add(response);
        }

        return result;
    }

    /**
     * 获取问卷详情
     */
    public QuestionnaireDetailResponse getQuestionnaireDetail(Long questionnaireId, Long userId) {
        // 获取学生信息
        Student student = studentMapper.selectOne(
            new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId)
        );
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }

        // 检查是否已提交
        QuestionnaireSubmission submission = submissionMapper.selectOne(
            new LambdaQueryWrapper<QuestionnaireSubmission>()
                .eq(QuestionnaireSubmission::getQuestionnaireId, questionnaireId)
                .eq(QuestionnaireSubmission::getStudentId, student.getId())
        );
        if (submission != null) {
            throw new BusinessException("该问卷已提交，不能重复作答");
        }

        // 获取问卷信息
        Questionnaire questionnaire = questionnaireMapper.selectById(questionnaireId);
        if (questionnaire == null) {
            throw new BusinessException("问卷不存在");
        }
        if (!"PUBLISHED".equals(questionnaire.getStatus())) {
            throw new BusinessException("问卷未发布");
        }

        // 检查截止时间
        if (questionnaire.getDeadline() != null && LocalDateTime.now().isAfter(questionnaire.getDeadline())) {
            throw new BusinessException("问卷已过截止时间");
        }

        // 获取题目列表
        List<Question> questions = questionMapper.selectList(
            new LambdaQueryWrapper<Question>()
                .eq(Question::getQuestionnaireId, questionnaireId)
                .orderByAsc(Question::getQuestionOrder)
        );

        // 构建响应
        QuestionnaireDetailResponse response = new QuestionnaireDetailResponse();
        response.setId(questionnaire.getId());
        response.setTitle(questionnaire.getTitle());
        response.setDescription(questionnaire.getDescription());
        response.setTimeLimit(questionnaire.getTimeLimit());
        response.setTotalScore(questionnaire.getTotalScore());
        response.setStartTime(questionnaire.getStartTime());
        response.setDeadline(questionnaire.getDeadline());

        List<QuestionnaireDetailResponse.QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question q : questions) {
            QuestionnaireDetailResponse.QuestionDTO dto = new QuestionnaireDetailResponse.QuestionDTO();
            dto.setId(q.getId());
            dto.setQuestionOrder(q.getQuestionOrder());
            dto.setType(q.getType());
            dto.setContent(q.getContent());
            dto.setKnowledgePoint(q.getKnowledgePoint());
            dto.setDifficulty(q.getDifficulty());
            dto.setScore(q.getScore());

            // 解析选项
            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                try {
                    List<String> options = objectMapper.readValue(q.getOptions(), new TypeReference<List<String>>() {});
                    dto.setOptions(options);
                } catch (Exception e) {
                    log.error("解析选项失败", e);
                    dto.setOptions(new ArrayList<>());
                }
            }

            questionDTOs.add(dto);
        }
        response.setQuestions(questionDTOs);

        return response;
    }

    /**
     * 提交答案
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitAnswers(SubmitAnswersRequest request, Long userId) {
        // 获取学生信息
        Student student = studentMapper.selectOne(
            new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId)
        );
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }

        // 检查是否已提交
        QuestionnaireSubmission existingSubmission = submissionMapper.selectOne(
            new LambdaQueryWrapper<QuestionnaireSubmission>()
                .eq(QuestionnaireSubmission::getQuestionnaireId, request.getQuestionnaireId())
                .eq(QuestionnaireSubmission::getStudentId, student.getId())
        );
        if (existingSubmission != null) {
            throw new BusinessException("该问卷已提交，不能重复提交");
        }

        // 获取问卷信息
        Questionnaire questionnaire = questionnaireMapper.selectById(request.getQuestionnaireId());
        if (questionnaire == null) {
            throw new BusinessException("问卷不存在");
        }

        // 获取所有题目
        List<Question> questions = questionMapper.selectList(
            new LambdaQueryWrapper<Question>()
                .eq(Question::getQuestionnaireId, request.getQuestionnaireId())
        );
        Map<Long, Question> questionMap = questions.stream()
            .collect(Collectors.toMap(Question::getId, q -> q));

        // 批改答案并保存
        BigDecimal totalScore = BigDecimal.ZERO;
        for (SubmitAnswersRequest.AnswerDTO answerDTO : request.getAnswers()) {
            Question question = questionMap.get(answerDTO.getQuestionId());
            if (question == null) {
                continue;
            }

            // 评分
            GradingResult gradingResult = gradingService.gradeAnswer(question, answerDTO.getAnswerContent());

            // 保存答案
            Answer answer = new Answer();
            answer.setQuestionnaireId(request.getQuestionnaireId());
            answer.setQuestionId(answerDTO.getQuestionId());
            answer.setStudentId(student.getId());
            answer.setAnswerContent(answerDTO.getAnswerContent());
            answer.setIsCorrect(gradingResult.isCorrect());
            answer.setScore(gradingResult.getScore());
            answer.setFeedback(gradingResult.getFeedback());
            answer.setTimeSpent(answerDTO.getTimeSpent());
            answerMapper.insert(answer);

            totalScore = totalScore.add(gradingResult.getScore());
        }

        // 保存提交记录
        QuestionnaireSubmission submission = new QuestionnaireSubmission();
        submission.setQuestionnaireId(request.getQuestionnaireId());
        submission.setStudentId(student.getId());
        submission.setTotalScore(totalScore);
        submission.setTimeSpent(request.getTimeSpent());
        submission.setSubmitTime(LocalDateTime.now());
        submissionMapper.insert(submission);

        // 更新学生总分
        BigDecimal currentTotal = student.getTotalScores() != null ? student.getTotalScores() : BigDecimal.ZERO;
        student.setTotalScores(currentTotal.add(totalScore));
        studentMapper.updateById(student);

        log.info("学生 {} 提交问卷 {}，得分 {}", student.getId(), request.getQuestionnaireId(), totalScore);
    }

    /**
     * 获取问卷结果
     */
    public QuestionnaireResultResponse getQuestionnaireResult(Long questionnaireId, Long userId) {
        // 获取学生信息
        Student student = studentMapper.selectOne(
            new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId)
        );
        if (student == null) {
            throw new BusinessException("学生信息不存在");
        }

        // 获取提交记录
        QuestionnaireSubmission submission = submissionMapper.selectOne(
            new LambdaQueryWrapper<QuestionnaireSubmission>()
                .eq(QuestionnaireSubmission::getQuestionnaireId, questionnaireId)
                .eq(QuestionnaireSubmission::getStudentId, student.getId())
        );
        if (submission == null) {
            throw new BusinessException("未找到提交记录");
        }

        // 获取问卷信息
        Questionnaire questionnaire = questionnaireMapper.selectById(questionnaireId);
        if (questionnaire == null) {
            throw new BusinessException("问卷不存在");
        }

        // 获取所有题目
        List<Question> questions = questionMapper.selectList(
            new LambdaQueryWrapper<Question>()
                .eq(Question::getQuestionnaireId, questionnaireId)
                .orderByAsc(Question::getQuestionOrder)
        );

        // 获取学生的答案
        List<Answer> answers = answerMapper.selectList(
            new LambdaQueryWrapper<Answer>()
                .eq(Answer::getQuestionnaireId, questionnaireId)
                .eq(Answer::getStudentId, student.getId())
        );
        Map<Long, Answer> answerMap = answers.stream()
            .collect(Collectors.toMap(Answer::getQuestionId, a -> a));

        // 构建响应
        QuestionnaireResultResponse response = new QuestionnaireResultResponse();
        response.setId(questionnaire.getId());
        response.setTitle(questionnaire.getTitle());
        response.setTotalScore(questionnaire.getTotalScore());
        response.setMyScore(submission.getTotalScore());
        response.setPassScore(questionnaire.getPassScore());
        response.setIsPassed(submission.getTotalScore().compareTo(questionnaire.getPassScore()) >= 0);
        response.setTimeSpent(submission.getTimeSpent());
        response.setSubmitTime(submission.getSubmitTime());

        List<QuestionnaireResultResponse.QuestionResultDTO> questionResults = new ArrayList<>();
        for (Question q : questions) {
            Answer answer = answerMap.get(q.getId());

            QuestionnaireResultResponse.QuestionResultDTO dto = new QuestionnaireResultResponse.QuestionResultDTO();
            dto.setQuestionId(q.getId());
            dto.setQuestionOrder(q.getQuestionOrder());
            dto.setType(q.getType());
            dto.setContent(q.getContent());
            dto.setCorrectAnswer(q.getCorrectAnswer());
            dto.setMaxScore(q.getScore());
            dto.setExplanation(q.getExplanation());
            dto.setKnowledgePoint(q.getKnowledgePoint());

            // 解析选项
            if (q.getOptions() != null && !q.getOptions().isEmpty()) {
                try {
                    List<String> options = objectMapper.readValue(q.getOptions(), new TypeReference<List<String>>() {});
                    dto.setOptions(options);
                } catch (Exception e) {
                    log.error("解析选项失败", e);
                    dto.setOptions(new ArrayList<>());
                }
            }

            if (answer != null) {
                dto.setMyAnswer(answer.getAnswerContent());
                dto.setIsCorrect(answer.getIsCorrect());
                dto.setScore(answer.getScore());
                dto.setFeedback(answer.getFeedback());
            } else {
                dto.setMyAnswer("");
                dto.setIsCorrect(false);
                dto.setScore(BigDecimal.ZERO);
                dto.setFeedback("未作答");
            }

            questionResults.add(dto);
        }
        response.setQuestionResults(questionResults);

        return response;
    }
}
