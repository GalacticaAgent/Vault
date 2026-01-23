package com.vault.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RAG 功能测试
 * 启动时自动运行（仅在开发环境）
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "vault.rag.test-on-startup", havingValue = "true")
public class RAGTester implements CommandLineRunner {
    
    private final RAGService ragService;
    
    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("开始 RAG 功能测试");
        log.info("========================================");
        
        try {
            // 测试 1: 索引测试文本
            testIndexing();
            
            // 测试 2: 检索测试
            testSearch();
            
            log.info("========================================");
            log.info("✅ RAG 功能测试完成");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("❌ RAG 功能测试失败", e);
        }
    }
    
    private void testIndexing() {
        log.info("\n📝 测试 1: 文本索引");
        log.info("---");
        
        String testContent = """
            操作系统是管理计算机硬件与软件资源的计算机程序。
            操作系统需要处理如管理与配置内存、决定系统资源供需的优先次序、
            控制输入设备与输出设备、操作网络与管理文件系统等基本事务。
            
            进程是计算机中的程序关于某数据集合上的一次运行活动，
            是系统进行资源分配和调度的基本单位，是操作系统结构的基础。
            
            线程是操作系统能够进行运算调度的最小单位。
            它被包含在进程之中，是进程中的实际运作单位。
            """;
        
        try {
            ragService.indexText(999L, "操作系统测试教材", testContent);
            log.info("✅ 文本索引成功");
        } catch (Exception e) {
            log.warn("⚠️  文本索引失败（可能 Milvus 未启动）: {}", e.getMessage());
        }
    }
    
    private void testSearch() {
        log.info("\n🔍 测试 2: 语义检索");
        log.info("---");
        
        String[] queries = {
            "什么是进程？",
            "线程和进程有什么区别？",
            "操作系统的主要功能是什么？"
        };
        
        for (String query : queries) {
            log.info("查询: {}", query);
            
            try {
                RAGSearchResult result = ragService.search(query, 3);
                
                if (result.getChunks().isEmpty()) {
                    log.warn("  ⚠️  未找到相关内容（可能 Milvus 未启动或无数据）");
                } else {
                    log.info("  ✅ 找到 {} 条相关内容，耗时 {}ms", 
                            result.getChunks().size(), 
                            result.getSearchTime());
                    
                    for (int i = 0; i < Math.min(2, result.getChunks().size()); i++) {
                        RAGSearchResult.TextChunk chunk = result.getChunks().get(i);
                        log.info("    [{}] {} - {} (相似度: {:.2f})", 
                                i + 1,
                                chunk.getMaterialName(),
                                chunk.getChapter(),
                                chunk.getScore());
                    }
                }
            } catch (Exception e) {
                log.error("  ❌ 检索失败: {}", e.getMessage());
            }
            
            log.info("");
        }
    }
}
