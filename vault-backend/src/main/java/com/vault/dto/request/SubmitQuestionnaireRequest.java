package com.vault.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 提交问卷请求
 */
@Data
@Schema(description = "提交问卷请求")
public class SubmitQuestionnaireRequest {
    
    @Schema(description = "问卷答案列表")
    @NotNull(message = "答案不能为空")
    private List<QuestionAnswer> answers;
    
    @Data
    @Schema(description = "问题答案")
    public static class QuestionAnswer {
        
        @Schema(description = "问题ID")
        @NotNull(message = "问题ID不能为空")
        private Long questionId;
        
        @Schema(description = "答案内容")
        @NotNull(message = "答案不能为空")
        private String answer;
        
        @Schema(description = "答题时长（秒）")
        private Integer duration;
    }
}
