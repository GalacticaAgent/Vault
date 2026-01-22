package com.vault.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RAG (Retrieval-Augmented Generation) 检索服务
 * 负责从向量数据库中检索相关的教材内容
 */
@Slf4j
@Service
public class RAGService {
    
    @Value("${vault.milvus.host:localhost}")
    private String milvusHost;
    
    @Value("${vault.milvus.port:19530}")
    private int milvusPort;
    
    @Value("${vault.embedding.model:text-embedding-ada-002}")
    private String embeddingModel;
    
    // 这里应该注入Milvus客户端
    // private MilvusClient milvusClient;
    
    /**
     * 搜索相关资料
     */
    public RAGSearchResult search(String query, int topK) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 将查询转换为向量
            float[] queryVector = getEmbedding(query);
            
            // 2. 在Milvus中进行向量检索
            List<RAGSearchResult.TextChunk> chunks = searchVectors(queryVector, topK);
            
            // 3. 构建结果
            RAGSearchResult result = new RAGSearchResult();
            result.setChunks(chunks);
            result.setSearchTime(System.currentTimeMillis() - startTime);
            
            log.info("RAG search completed: {} chunks found in {}ms", 
                    chunks.size(), result.getSearchTime());
            
            return result;
            
        } catch (Exception e) {
            log.error("RAG search failed for query: {}", query, e);
            throw new RuntimeException("RAG search failed", e);
        }
    }
    
    /**
     * 获取文本的向量表示
     */
    private float[] getEmbedding(String text) {
        // 这里应该调用OpenAI Embedding API或其他embedding服务
        // 简化处理：返回模拟向量
        float[] vector = new float[1536]; // OpenAI embedding维度
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) Math.random();
        }
        return vector;
    }
    
    /**
     * 在向量数据库中检索
     */
    private List<RAGSearchResult.TextChunk> searchVectors(float[] queryVector, int topK) {
        // 实际实现应该调用Milvus API
        // 这里返回模拟数据
        
        List<RAGSearchResult.TextChunk> chunks = new ArrayList<>();
        
        // 模拟检索结果
        for (int i = 0; i < Math.min(topK, 5); i++) {
            RAGSearchResult.TextChunk chunk = new RAGSearchResult.TextChunk();
            chunk.setMaterialId(1L);
            chunk.setMaterialName("操作系统原理");
            chunk.setChapter("第" + (i + 1) + "章");
            chunk.setContent("这是一段示例内容...");
            chunk.setScore(0.9 - i * 0.1);
            chunk.setPosition(i * 100);
            chunk.setVectorId("vec_" + i);
            chunks.add(chunk);
        }
        
        return chunks;
    }
    
    /**
     * 索引新的文本内容
     */
    public void indexText(Long materialId, String materialName, String content) {
        try {
            // 1. 分块
            List<String> chunks = splitText(content);
            
            // 2. 生成向量
            List<float[]> vectors = new ArrayList<>();
            for (String chunk : chunks) {
                vectors.add(getEmbedding(chunk));
            }
            
            // 3. 插入Milvus
            // insertToMilvus(materialId, materialName, chunks, vectors);
            
            log.info("Indexed {} chunks for material: {}", chunks.size(), materialName);
            
        } catch (Exception e) {
            log.error("Failed to index text for material: {}", materialId, e);
            throw new RuntimeException("Text indexing failed", e);
        }
    }
    
    /**
     * 文本分块
     */
    private List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        
        // 简单的分块策略：每500字符一块，重叠100字符
        int chunkSize = 500;
        int overlap = 100;
        
        for (int i = 0; i < text.length(); i += (chunkSize - overlap)) {
            int end = Math.min(i + chunkSize, text.length());
            chunks.add(text.substring(i, end));
            
            if (end == text.length()) {
                break;
            }
        }
        
        return chunks;
    }
    
    /**
     * 删除资料的索引
     */
    public void deleteIndex(Long materialId) {
        try {
            // 从Milvus删除该资料的所有向量
            log.info("Deleted index for material: {}", materialId);
        } catch (Exception e) {
            log.error("Failed to delete index for material: {}", materialId, e);
        }
    }
    
    /**
     * 构建用于LLM的上下文
     */
    public String buildContext(RAGSearchResult searchResult) {
        if (searchResult == null || searchResult.getChunks().isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("相关参考资料：\n\n");
        
        for (int i = 0; i < searchResult.getChunks().size(); i++) {
            RAGSearchResult.TextChunk chunk = searchResult.getChunks().get(i);
            context.append(String.format("[参考资料%d] %s - %s\n%s\n\n",
                    i + 1,
                    chunk.getMaterialName(),
                    chunk.getChapter(),
                    chunk.getContent()));
        }
        
        return context.toString();
    }
}
