# AI Service

## 职责
处理 AI 相关的业务逻辑，包括 LLM 调用、向量化、RAG 检索等。

## 主要服务

### LLMService.java
大语言模型服务
- 调用 OpenAI/Claude API
- 流式响应处理
- Token 计数
- 多模型支持

### EmbeddingService.java
向量化服务
- 文本向量化
- 向量存储
- 向量检索

### RAGService.java
RAG 检索增强生成服务
- 知识图谱检索
- 向量相似度检索
- 全文检索
- 上下文构建

### SkillExecutor.java
Skill 执行器
- Skill 解析
- Skill 执行
- Skill 结果处理

