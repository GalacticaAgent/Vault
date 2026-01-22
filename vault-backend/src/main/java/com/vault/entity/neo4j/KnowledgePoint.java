package com.vault.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 知识点实体类（Neo4j）
 * 表示课程中的一个知识点
 */
@Data
@Node("KnowledgePoint")
public class KnowledgePoint {
    
    @Id
    @GeneratedValue
    private Long id;
    
    /**
     * 知识点名称
     */
    @Property("name")
    private String name;
    
    /**
     * 知识点描述
     */
    @Property("description")
    private String description;
    
    /**
     * 所属课程
     */
    @Property("course")
    private String course;
    
    /**
     * 所属章节
     */
    @Property("chapter")
    private String chapter;
    
    /**
     * 难度等级：1-5
     */
    @Property("difficulty")
    private Integer difficulty;
    
    /**
     * 重要程度：1-5
     */
    @Property("importance")
    private Integer importance;
    
    /**
     * 关键词（用于检索）
     */
    @Property("keywords")
    private String keywords;
}
