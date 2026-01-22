package com.vault.service.questionnaire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.dto.request.CreateQuestionnaireRequest;
import com.vault.dto.request.GenerateQuestionnaireRequest;
import com.vault.dto.response.QuestionnaireDetailResponse;
import com.vault.exception.BusinessException;
import com.vault.service.ai.DeepSeekService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 问卷生成服务类（AI 功能）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionGeneratorService {

    private final DeepSeekService deepSeekService;
    private final QuestionnaireService questionnaireService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * AI 生成问卷
     */
    public QuestionnaireDetailResponse generateQuestionnaire(
            Long teacherId, GenerateQuestionnaireRequest request) {
        try {
            log.info("开始 AI 生成问卷: teacherId={}, title={}, knowledgePoints={}, difficulty={}, questionCount={}",
                     teacherId, request.getTitle(), request.getKnowledgePoints(),
                     request.getDifficulty(), request.getQuestionCount());

            // 1. 构建 AI prompt
            String prompt = buildPrompt(request);

            // 2. 调用 DeepSeek API 生成题目
            String aiResponse = deepSeekService.chat(prompt);

            // 3. 解析 AI 返回的 JSON
            List<CreateQuestionnaireRequest.QuestionDTO> questions = parseAiResponse(aiResponse, request.getQuestionCount());

            // 4. 构建问卷请求
            CreateQuestionnaireRequest createRequest = new CreateQuestionnaireRequest();
            createRequest.setTitle(request.getTitle());
            createRequest.setDescription(request.getDescription());
            createRequest.setTimeLimit(60); // 默认60分钟
            createRequest.setPassScore(new BigDecimal("60.00"));
            createRequest.setStartTime(LocalDateTime.now());
            createRequest.setDeadline(LocalDateTime.now().plusDays(7)); // 默认7天后截止
            createRequest.setTargetStudents(request.getTargetStudents());
            createRequest.setQuestions(questions);

            // 5. 调用问卷服务创建问卷
            QuestionnaireDetailResponse response = questionnaireService.createQuestionnaire(teacherId, createRequest);

            log.info("AI 生成问卷成功: questionnaireId={}, questionCount={}",
                     response.getId(), response.getQuestions().size());

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 生成问卷失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "AI 生成问卷失败: " + e.getMessage());
        }
    }

    /**
     * 构建 AI prompt
     */
    private String buildPrompt(GenerateQuestionnaireRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下要求生成").append(request.getQuestionCount()).append("道题目：\n\n");
        prompt.append("知识点：").append(String.join("、", request.getKnowledgePoints())).append("\n");
        prompt.append("难度：").append(getDifficultyDescription(request.getDifficulty())).append("\n\n");

        prompt.append("题目类型要求：\n");
        prompt.append("- 单选题（SINGLE_CHOICE）：占40%\n");
        prompt.append("- 多选题（MULTIPLE_CHOICE）：占30%\n");
        prompt.append("- 判断题（TRUE_FALSE）：占20%\n");
        prompt.append("- 简答题（SHORT_ANSWER）：占10%\n\n");

        prompt.append("请以 JSON 数组格式返回，每道题包含以下字段：\n");
        prompt.append("{\n");
        prompt.append("  \"type\": \"题目类型（SINGLE_CHOICE/MULTIPLE_CHOICE/TRUE_FALSE/SHORT_ANSWER）\",\n");
        prompt.append("  \"content\": \"题目内容\",\n");
        prompt.append("  \"options\": [\"选项A\", \"选项B\", \"选项C\", \"选项D\"] // 选择题和判断题需要，判断题为[\"正确\", \"错误\"]\n");
        prompt.append("  \"correctAnswer\": \"正确答案（单选题为单个选项，多选题用逗号分隔，判断题为'正确'或'错误'）\",\n");
        prompt.append("  \"knowledgePoint\": \"关联知识点\",\n");
        prompt.append("  \"difficulty\": \"").append(request.getDifficulty()).append("\",\n");
        prompt.append("  \"score\": 题目分值（单选5分、多选10分、判断5分、简答15分）,\n");
        prompt.append("  \"explanation\": \"题目解析\"\n");
        prompt.append("}\n\n");

        prompt.append("注意事项：\n");
        prompt.append("1. 只返回 JSON 数组，不要包含任何其他文字说明\n");
        prompt.append("2. 题目要准确、清晰、符合教学要求\n");
        prompt.append("3. 选项要合理，干扰项要有迷惑性\n");
        prompt.append("4. 解析要详细、准确\n");
        prompt.append("5. 多选题的 correctAnswer 格式示例：\"选项A,选项B,选项C\"\n");
        prompt.append("6. 确保 JSON 格式正确，可以直接解析\n");

        return prompt.toString();
    }

    /**
     * 解析 AI 返回的响应
     */
    private List<CreateQuestionnaireRequest.QuestionDTO> parseAiResponse(String aiResponse, Integer expectedCount) {
        try {
            // 提取 JSON 部分（AI 可能返回带有说明文字的内容）
            String jsonContent = extractJson(aiResponse);

            // 解析 JSON
            List<Map<String, Object>> rawQuestions = objectMapper.readValue(
                    jsonContent,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<CreateQuestionnaireRequest.QuestionDTO> questions = new ArrayList<>();

            for (Map<String, Object> raw : rawQuestions) {
                CreateQuestionnaireRequest.QuestionDTO question = new CreateQuestionnaireRequest.QuestionDTO();
                question.setType((String) raw.get("type"));
                question.setContent((String) raw.get("content"));

                // 处理 options（可能是 List 或 null）
                Object optionsObj = raw.get("options");
                if (optionsObj instanceof List) {
                    question.setOptions((List<String>) optionsObj);
                }

                question.setCorrectAnswer((String) raw.get("correctAnswer"));
                question.setKnowledgePoint((String) raw.get("knowledgePoint"));
                question.setDifficulty((String) raw.get("difficulty"));

                // 处理分值（可能是 Integer 或 Double）
                Object scoreObj = raw.get("score");
                if (scoreObj instanceof Number) {
                    question.setScore(new BigDecimal(scoreObj.toString()));
                }

                question.setExplanation((String) raw.get("explanation"));

                questions.add(question);
            }

            if (questions.size() != expectedCount) {
                log.warn("AI 生成题目数量不符合预期: expected={}, actual={}", expectedCount, questions.size());
            }

            return questions;

        } catch (Exception e) {
            log.error("解析 AI 响应失败: {}", e.getMessage(), e);
            log.error("AI 原始响应: {}", aiResponse);
            throw new BusinessException(500, "解析 AI 响应失败，请重试");
        }
    }

    /**
     * 提取 JSON 内容
     */
    private String extractJson(String response) {
        // 尝试提取 JSON 数组部分
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        // 如果没有找到数组，尝试整个内容
        return response.trim();
    }

    /**
     * 获取难度描述
     */
    private String getDifficultyDescription(String difficulty) {
        return switch (difficulty) {
            case "EASY" -> "简单（适合基础知识点）";
            case "MEDIUM" -> "中等（适合综合应用）";
            case "HARD" -> "困难（适合深入理解和拓展）";
            default -> "中等";
        };
    }
}
