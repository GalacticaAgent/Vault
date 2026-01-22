package com.vault.dto.response;

<<<<<<< HEAD
import lombok.Data;
=======
import lombok.Builder;
import lombok.Data;

>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
<<<<<<< HEAD
 * 问卷详情响应DTO
 */
@Data
public class QuestionnaireDetailResponse {

    private Long id;
    private String title;
    private String description;
    private Integer timeLimit;
    private BigDecimal totalScore;
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Long id;
        private Integer questionOrder;
        private String type;
        private String content;
        private List<String> options;
        private String knowledgePoint;
        private String difficulty;
        private BigDecimal score;
=======
 * 问卷详情响应 DTO
 */
@Data
@Builder
public class QuestionnaireDetailResponse {

    /**
     * 问卷ID
     */
    private Long id;

    /**
     * 问卷标题
     */
    private String title;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 时间限制（分钟）
     */
    private Integer timeLimit;

    /**
     * 总分
     */
    private BigDecimal totalScore;

    /**
     * 及格分数
     */
    private BigDecimal passScore;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 状态
     */
    private String status;

    /**
     * 目标学生ID列表
     */
    private List<Long> targetStudents;

    /**
     * 目标学生数量
     */
    private Integer targetStudentCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 题目列表
     */
    private List<QuestionVO> questions;

    /**
     * 题目视图对象
     */
    @Data
    @Builder
    public static class QuestionVO {

        /**
         * 题目ID
         */
        private Long id;

        /**
         * 题目顺序
         */
        private Integer questionOrder;

        /**
         * 题目类型
         */
        private String type;

        /**
         * 题目内容
         */
        private String content;

        /**
         * 选项列表
         */
        private List<String> options;

        /**
         * 正确答案（仅教师可见）
         */
        private String correctAnswer;

        /**
         * 关联知识点
         */
        private String knowledgePoint;

        /**
         * 难度
         */
        private String difficulty;

        /**
         * 分值
         */
        private BigDecimal score;

        /**
         * 题目解析
         */
        private String explanation;
>>>>>>> a7cea79f21da3f3fdf35017c3222bcd9e3417aed
    }
}
