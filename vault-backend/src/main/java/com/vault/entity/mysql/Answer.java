package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 答案实体类
 */
@Data
@TableName("answers")
public class Answer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionnaireId;

    private Long questionId;

    private Long studentId;

    private String answerContent;

    private Boolean isCorrect;

    private BigDecimal score;

    private String feedback;

    private Integer timeSpent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
