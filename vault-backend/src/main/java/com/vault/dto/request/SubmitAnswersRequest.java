package com.vault.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.List;

/**
 * 提交答案请求DTO
 */
@Data
public class SubmitAnswersRequest {

    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @NotNull(message = "答题用时不能为空")
    @Positive(message = "答题用时必须大于0")
    private Integer timeSpent;

    @NotEmpty(message = "答案列表不能为空")
    @Valid
    private List<AnswerDTO> answers;

    @Data
    public static class AnswerDTO {
        @NotNull(message = "题目ID不能为空")
        private Long questionId;

        private String answerContent;

        private Integer timeSpent;
    }
}
