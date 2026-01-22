package com.vault.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;

/**
 * 学生知识点掌握关系（Neo4j）
 * 表示学生对某个知识点的掌握情况
 */
@Data
@RelationshipProperties
public class StudentKnowledge {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 掌握程度：0.0-1.0
     */
    @Property("mastery")
    private Double mastery;
    
    /**
     * 练习次数
     */
    @Property("practiceCount")
    private Integer practiceCount;
    
    /**
     * 正确次数
     */
    @Property("correctCount")
    private Integer correctCount;
    
    /**
     * 最后练习时间
     */
    @Property("lastPracticeTime")
    private LocalDateTime lastPracticeTime;
    
    /**
     * 学习时长（分钟）
     */
    @Property("studyDuration")
    private Integer studyDuration;
    
    /**
     * 薄弱指数：0.0-1.0，越高表示越薄弱
     */
    @Property("weaknessIndex")
    private Double weaknessIndex;
}
