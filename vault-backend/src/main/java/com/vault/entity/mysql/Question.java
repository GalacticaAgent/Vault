package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 题目实体类
 */
@Data
@TableName("questions")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionnaireId;

    private Integer questionOrder;

    private String type;

    private String content;

    private String options;

    private String correctAnswer;

    private String knowledgePoint;

    private String difficulty;

    private BigDecimal score;

    private String explanation;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
