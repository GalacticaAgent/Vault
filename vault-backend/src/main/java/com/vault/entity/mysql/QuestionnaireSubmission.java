package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷提交记录实体类
 */
@Data
@TableName("questionnaire_submissions")
public class QuestionnaireSubmission {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionnaireId;

    private Long studentId;

    private BigDecimal totalScore;

    private Integer timeSpent;

    private LocalDateTime submitTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
