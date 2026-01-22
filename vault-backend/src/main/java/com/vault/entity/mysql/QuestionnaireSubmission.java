package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
