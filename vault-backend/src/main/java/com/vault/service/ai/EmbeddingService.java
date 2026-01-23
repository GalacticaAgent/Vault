package com.vault.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding 服务 - 将文本转换为向量
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    
    private final ObjectMapper objectMapper;
    
    @Value("${vault.embedding.provider:openai}")
    private String provider;
    
    @Value("${vault.embedding.api-key:}")
    private String apiKey;
    
    @Value("${vault.embedding.api-url:https://api.openai.com/v1/embeddings}")
    private String apiUrl;
    
    @Value("${vault.embedding.model:text-embedding-ada-002}")
    private String model;
    
    @Value("${vault.embedding.enabled:false}")
    private boolean embeddingEnabled;
    
    /**
     * 获取文本的向量表示
     */
    public float[] getEmbedding(String text) {
        if (!embeddingEnabled || apiKey == null || apiKey.isEmpty()) {
            log.warn("Embedding 服务未启用，返回随机向量");
            return generateRandomVector(1536);
        }
        
        try {
            return callEmbeddingAPI(text);
        } catch (Exception e) {
            log.error("调用 Embedding API 失败，使用随机向量: {}", e.getMessage());
            return generateRandomVector(1536);
        }
    }
    
    /**
     * 批量获取文本向量
     */
    public List<float[]> getEmbeddings(List<String> texts) {
        List<float[]> embeddings = new ArrayList<>();
        for (String text : texts) {
            embeddings.add(getEmbedding(text));
        }
        return embeddings;
    }
    
    /**
     * 调用 Embedding API
     */
    private float[] callEmbeddingAPI(String text) throws IOException {
        switch (provider.toLowerCase()) {
            case "openai":
                return callOpenAIEmbedding(text);
            case "local":
                return callLocalEmbedding(text);
            default:
                throw new IllegalArgumentException("不支持的 Embedding 提供商: " + provider);
        }
    }
    
    /**
     * 调用 OpenAI Embedding API
     */
    private float[] callOpenAIEmbedding(String text) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("input", text);
            payload.put("model", model);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            request.setEntity(new StringEntity(jsonPayload));
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                
                // 解析向量数据
                JsonNode embeddingNode = jsonResponse
                        .path("data")
                        .path(0)
                        .path("embedding");
                
                if (embeddingNode.isMissingNode()) {
                    throw new IOException("API 响应中未找到 embedding 数据");
                }
                
                // 转换为 float 数组
                int dim = embeddingNode.size();
                float[] vector = new float[dim];
                for (int i = 0; i < dim; i++) {
                    vector[i] = (float) embeddingNode.get(i).asDouble();
                }
                
                log.debug("成功获取 Embedding，维度: {}", dim);
                return vector;
            }
        }
    }
    
    /**
     * 调用本地 Embedding 服务
     */
    private float[] callLocalEmbedding(String text) throws IOException {
        // TODO: 实现本地 Embedding 服务调用
        // 可以使用 Python 的 sentence-transformers
        throw new UnsupportedOperationException("本地 Embedding 服务尚未实现");
    }
    
    /**
     * 生成随机向量（用于测试）
     */
    private float[] generateRandomVector(int dimension) {
        float[] vector = new float[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = (float) (Math.random() * 2 - 1); // [-1, 1]
        }
        return vector;
    }
    
    /**
     * 计算向量相似度（余弦相似度）
     */
    public double cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
