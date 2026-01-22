package com.vault.service.chat;

import lombok.Data;
import java.util.List;

/**
 * RAG检索结果
 */
@Data
public class RAGSearchResult {
    
    /**
     * 检索到的文本块列表
     */
    private List<TextChunk> chunks;
    
    /**
     * 检索耗时（毫秒）
     */
    private long searchTime;
    
    /**
     * 文本块
     */
    @Data
    public static class TextChunk {
        
        /**
         * 资料ID
         */
        private Long materialId;
        
        /**
         * 资料名称
         */
        private String materialName;
        
        /**
         * 章节
         */
        private String chapter;
        
        /**
         * 文本内容
         */
        private String content;
        
        /**
         * 相关性分数
         */
        private Double score;
        
        /**
         * 在原文中的位置
         */
        private Integer position;
        
        /**
         * 向量ID
         */
        private String vectorId;
    }
}
