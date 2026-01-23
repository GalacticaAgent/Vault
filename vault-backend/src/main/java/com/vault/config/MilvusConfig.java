package com.vault.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.grpc.DataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Arrays;

/**
 * Milvus 向量数据库配置
 */
@Slf4j
@Configuration
public class MilvusConfig {
    
    @Value("${milvus.host:localhost}")
    private String milvusHost;
    
    @Value("${milvus.port:19530}")
    private int milvusPort;
    
    @Value("${vault.milvus.collection:vault_materials}")
    private String collectionName;
    
    @Value("${vault.milvus.vector-dim:1536}")
    private int vectorDim;
    
    @Value("${vault.milvus.enabled:true}")
    private boolean milvusEnabled;
    
    private MilvusServiceClient milvusClient;
    
    @Bean
    public MilvusServiceClient milvusClient() {
        if (!milvusEnabled) {
            log.warn("Milvus is disabled. RAG功能将使用模拟数据");
            return null;
        }
        
        try {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(milvusHost)
                    .withPort(milvusPort)
                    .build();
            
            milvusClient = new MilvusServiceClient(connectParam);
            log.info("成功连接到 Milvus: {}:{}", milvusHost, milvusPort);
            
            // 初始化 Collection
            initializeCollection();
            
            return milvusClient;
            
        } catch (Exception e) {
            log.error("连接 Milvus 失败: {}. RAG功能将使用模拟数据", e.getMessage());
            return null;
        }
    }
    
    /**
     * 初始化 Collection
     */
    private void initializeCollection() {
        try {
            // 检查 Collection 是否存在
            HasCollectionParam hasParam = HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();
            
            boolean hasCollection = milvusClient.hasCollection(hasParam).getData();
            
            if (hasCollection) {
                log.info("Collection '{}' 已存在", collectionName);
                return;
            }
            
            // 创建 Collection
            log.info("创建 Collection '{}'...", collectionName);
            
            FieldType fieldId = FieldType.newBuilder()
                    .withName("id")
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(true)
                    .build();
            
            FieldType fieldMaterialId = FieldType.newBuilder()
                    .withName("material_id")
                    .withDataType(DataType.Int64)
                    .build();
            
            FieldType fieldMaterialName = FieldType.newBuilder()
                    .withName("material_name")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(200)
                    .build();
            
            FieldType fieldChapter = FieldType.newBuilder()
                    .withName("chapter")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(100)
                    .build();
            
            FieldType fieldContent = FieldType.newBuilder()
                    .withName("content")
                    .withDataType(DataType.VarChar)
                    .withMaxLength(65535)
                    .build();
            
            FieldType fieldPosition = FieldType.newBuilder()
                    .withName("position")
                    .withDataType(DataType.Int64)
                    .build();
            
            FieldType fieldEmbedding = FieldType.newBuilder()
                    .withName("embedding")
                    .withDataType(DataType.FloatVector)
                    .withDimension(vectorDim)
                    .build();
            
            CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription("Vault 教材向量存储")
                    .withFieldTypes(Arrays.asList(
                            fieldId,
                            fieldMaterialId,
                            fieldMaterialName,
                            fieldChapter,
                            fieldContent,
                            fieldPosition,
                            fieldEmbedding
                    ))
                    .build();
            
            milvusClient.createCollection(createParam);
            log.info("Collection '{}' 创建成功", collectionName);
            
            // 创建索引
            createIndex();
            
            // 加载 Collection 到内存
            loadCollection();
            
        } catch (Exception e) {
            log.error("初始化 Milvus Collection 失败", e);
        }
    }
    
    /**
     * 创建向量索引
     */
    private void createIndex() {
        try {
            log.info("为 '{}' 创建向量索引...", collectionName);
            
            CreateIndexParam indexParam = CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName("embedding")
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(MetricType.L2)
                    .withExtraParam("{\"nlist\":1024}")
                    .withSyncMode(Boolean.TRUE)
                    .build();
            
            milvusClient.createIndex(indexParam);
            log.info("向量索引创建成功");
            
        } catch (Exception e) {
            log.error("创建向量索引失败", e);
        }
    }
    
    /**
     * 加载 Collection 到内存
     */
    private void loadCollection() {
        try {
            log.info("加载 Collection '{}' 到内存...", collectionName);
            
            LoadCollectionParam loadParam = LoadCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build();
            
            milvusClient.loadCollection(loadParam);
            log.info("Collection 加载成功");
            
        } catch (Exception e) {
            log.error("加载 Collection 失败", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (milvusClient != null) {
            try {
                milvusClient.close();
                log.info("Milvus 连接已关闭");
            } catch (Exception e) {
                log.error("关闭 Milvus 连接失败", e);
            }
        }
    }
}
