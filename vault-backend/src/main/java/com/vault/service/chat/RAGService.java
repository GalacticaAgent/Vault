package com.vault.service.chat;

import com.vault.service.ai.EmbeddingService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.grpc.SearchResults;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * RAG (Retrieval-Augmented Generation) 检索服务
 * 负责从向量数据库中检索相关的教材内容
 */
@Slf4j
@Service
public class RAGService {
    
    @Autowired(required = false)
    private MilvusServiceClient milvusClient;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @Value("${vault.milvus.collection:vault_materials}")
    private String collectionName;
    
    @Value("${vault.milvus.search-params:16}")
    private int searchNProbe;
    
    /**
     * 搜索相关资料
     */
    public RAGSearchResult search(String query, int topK) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 将查询转换为向量
            float[] queryVector = embeddingService.getEmbedding(query);
            
            // 2. 在Milvus中进行向量检索
            List<RAGSearchResult.TextChunk> chunks;
            if (milvusClient != null) {
                chunks = searchVectorsInMilvus(queryVector, topK);
            } else {
                log.warn("Milvus 未连接，使用模拟数据");
                chunks = searchVectorsMock(queryVector, topK);
            }
            
            // 3. 构建结果
            RAGSearchResult result = new RAGSearchResult();
            result.setChunks(chunks);
            result.setSearchTime(System.currentTimeMillis() - startTime);
            
            log.info("RAG search completed: {} chunks found in {}ms", 
                    chunks.size(), result.getSearchTime());
            
            return result;
            
        } catch (Exception e) {
            log.error("RAG search failed for query: {}", query, e);
            // 失败时返回空结果而不是抛出异常
            RAGSearchResult result = new RAGSearchResult();
            result.setChunks(new ArrayList<>());
            result.setSearchTime(System.currentTimeMillis() - startTime);
            return result;
        }
    }
    
    /**
     * 在 Milvus 中检索向量
     */
    private List<RAGSearchResult.TextChunk> searchVectorsInMilvus(float[] queryVector, int topK) {
        try {
            // 构建搜索参数
            String searchParamStr = String.format("{\"nprobe\":%d}", searchNProbe);
            
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withVectorFieldName("embedding")
                    .withVectors(Collections.singletonList(queryVector))
                    .withTopK(topK)
                    .withParams(searchParamStr)
                    .withOutFields(Arrays.asList(
                            "material_id",
                            "material_name",
                            "chapter",
                            "content",
                            "position"
                    ))
                    .build();
            
            // 执行搜索
            io.milvus.common.clientenum.ConsistencyLevelEnum consistencyLevel = 
                    io.milvus.common.clientenum.ConsistencyLevelEnum.STRONG;
            
            io.milvus.grpc.SearchResults response = milvusClient.search(searchParam).getData();
            
            // 解析结果
            SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getResults());
            List<RAGSearchResult.TextChunk> chunks = new ArrayList<>();
            
            for (int i = 0; i < wrapper.getIDScore(0).size(); i++) {
                RAGSearchResult.TextChunk chunk = new RAGSearchResult.TextChunk();
                
                // 从字段中提取数据
                List<?> materialIds = (List<?>) wrapper.getFieldData("material_id", 0);
                List<?> materialNames = (List<?>) wrapper.getFieldData("material_name", 0);
                List<?> chapters = (List<?>) wrapper.getFieldData("chapter", 0);
                List<?> contents = (List<?>) wrapper.getFieldData("content", 0);
                List<?> positions = (List<?>) wrapper.getFieldData("position", 0);
                
                chunk.setMaterialId(Long.parseLong(materialIds.get(i).toString()));
                chunk.setMaterialName(materialNames.get(i).toString());
                chunk.setChapter(chapters.get(i).toString());
                chunk.setContent(contents.get(i).toString());
                chunk.setPosition(Integer.parseInt(positions.get(i).toString()));
                double score = wrapper.getIDScore(0).get(i).getScore();
                chunk.setScore(score);
                chunk.setVectorId(String.valueOf(wrapper.getIDScore(0).get(i).getLongID()));
                
                chunks.add(chunk);
            }
            
            log.debug("从 Milvus 检索到 {} 条结果", chunks.size());
            return chunks;
            
        } catch (Exception e) {
            log.error("Milvus 检索失败，使用模拟数据", e);
            return searchVectorsMock(queryVector, topK);
        }
    }
    
    /**
     * 模拟向量检索（用于 Milvus 未连接时）
     */
    private List<RAGSearchResult.TextChunk> searchVectorsMock(float[] queryVector, int topK) {
        List<RAGSearchResult.TextChunk> chunks = new ArrayList<>();
        
        // 返回模拟数据
        for (int i = 0; i < Math.min(topK, 3); i++) {
            RAGSearchResult.TextChunk chunk = new RAGSearchResult.TextChunk();
            chunk.setMaterialId(1L);
            chunk.setMaterialName("操作系统原理");
            chunk.setChapter("第" + (i + 1) + "章 - 进程管理");
            chunk.setContent("这是一段关于" + (i == 0 ? "进程概念" : i == 1 ? "进程调度" : "进程同步") + "的示例内容...");
            chunk.setScore(0.9 - i * 0.1);
            chunk.setPosition(i * 100);
            chunk.setVectorId("mock_vec_" + i);
            chunks.add(chunk);
        }
        
        return chunks;
    }
    
    /**
     * 索引新的文本内容到 Milvus
     */
    public void indexText(Long materialId, String materialName, String content) {
        try {
            if (milvusClient == null) {
                log.warn("Milvus 未连接，跳过索引");
                return;
            }
            
            // 1. 分块
            List<String> chunks = splitText(content);
            log.info("文本分块完成，共 {} 块", chunks.size());
            
            // 2. 生成向量
            List<float[]> vectors = embeddingService.getEmbeddings(chunks);
            log.info("向量生成完成，共 {} 个向量", vectors.size());
            
            // 3. 插入 Milvus
            insertToMilvus(materialId, materialName, chunks, vectors);
            
            log.info("成功索引 {} 个文本块到 Milvus，教材: {}", chunks.size(), materialName);
            
        } catch (Exception e) {
            log.error("索引文本失败，教材ID: {}", materialId, e);
            throw new RuntimeException("Text indexing failed", e);
        }
    }
    
    /**
     * 插入数据到 Milvus
     */
    private void insertToMilvus(Long materialId, String materialName, 
                                List<String> chunks, List<float[]> vectors) {
        try {
            List<Long> materialIds = new ArrayList<>();
            List<String> materialNames = new ArrayList<>();
            List<String> chapters = new ArrayList<>();
            List<String> contents = new ArrayList<>();
            List<Long> positions = new ArrayList<>();
            List<List<Float>> embeddings = new ArrayList<>();
            
            for (int i = 0; i < chunks.size(); i++) {
                materialIds.add(materialId);
                materialNames.add(materialName);
                chapters.add("第" + ((i / 10) + 1) + "章"); // 简单的章节划分
                contents.add(chunks.get(i));
                positions.add((long) (i * 500)); // 每块500字符
                
                // 转换 float[] 为 List<Float>
                List<Float> vectorList = new ArrayList<>();
                for (float v : vectors.get(i)) {
                    vectorList.add(v);
                }
                embeddings.add(vectorList);
            }
            
            // 构建插入参数
            List<InsertParam.Field> fields = new ArrayList<>();
            fields.add(new InsertParam.Field("material_id", materialIds));
            fields.add(new InsertParam.Field("material_name", materialNames));
            fields.add(new InsertParam.Field("chapter", chapters));
            fields.add(new InsertParam.Field("content", contents));
            fields.add(new InsertParam.Field("position", positions));
            fields.add(new InsertParam.Field("embedding", embeddings));
            
            InsertParam insertParam = InsertParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFields(fields)
                    .build();
            
            // 执行插入
            milvusClient.insert(insertParam);
            
            log.info("成功插入 {} 条记录到 Milvus（数据将自动持久化）", chunks.size());
            
        } catch (Exception e) {
            log.error("插入 Milvus 失败", e);
            throw new RuntimeException("Failed to insert to Milvus", e);
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
            if (milvusClient == null) {
                log.warn("Milvus 未连接，跳过删除");
                return;
            }
            
            // 构建删除表达式
            String expr = String.format("material_id == %d", materialId);
            
            io.milvus.param.dml.DeleteParam deleteParam = io.milvus.param.dml.DeleteParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withExpr(expr)
                    .build();
            
            milvusClient.delete(deleteParam);
            
            log.info("成功删除教材 {} 的向量索引", materialId);
            
        } catch (Exception e) {
            log.error("删除向量索引失败，教材ID: {}", materialId, e);
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
