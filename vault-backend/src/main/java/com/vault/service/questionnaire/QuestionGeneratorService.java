package com.vault.service.questionnaire;

import com.vault.dto.request.GenerateQuestionnaireRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 问卷生成服务类（AI 功能预留）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionGeneratorService {

    /**
     * AI 生成问卷（预留接口）
     */
    public QuestionnaireDetailResponse generateQuestionnaire(
            Long teacherId, GenerateQuestionnaireRequest request) {
        // TODO: 集成 AI 服务（OpenAI/通义千问）
        // 1. 根据知识点生成题目
        // 2. 根据难度调整题目
        // 3. 针对学生肖像生成定制题目
        log.warn("AI 生成问卷功能暂未实现");
        throw new BusinessException(501, "AI生成功能暂未实现，敬请期待");
    }
}
