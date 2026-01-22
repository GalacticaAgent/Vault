package com.vault.entity.mysql;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷实体类
 */
@Data
@TableName("questionnaires")
public class Questionnaire {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private Long creatorId;

    private String targetStudents;

    private Integer timeLimit;

    private BigDecimal totalScore;

    private BigDecimal passScore;

    private LocalDateTime startTime;

    private LocalDateTime deadline;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
