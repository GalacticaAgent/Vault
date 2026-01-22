# Service 层

## 职责
实现核心业务逻辑，处理数据转换，调用 Mapper 层进行数据持久化，集成外部服务。

## 主要服务

### UserService.java
- 用户注册、登录验证
- JWT Token 生成与验证
- 用户信息管理
- 密码加密存储（BCrypt）

### KnowledgeGraphService.java
- 知识图谱节点创建与管理
- 关系建立与查询
- 向量相似度检索（Milvus）
- 全文检索（Elasticsearch）
- 子图提取算法

### ChatService.java
- 会话创建与管理
- 消息处理与存储
- RAG（检索增强生成）实现
- 调用大模型 API
- 上下文管理（Redis 缓存）

### QuestionnaireService.java
- 问卷创建与发布
- AI 自动生成问卷
- 针对性题目生成（基于学生肖像）
- 答案提交与批改
- 统计分析

### CodeAnalysisService.java
- Git 仓库克隆与解析
- 代码质量分析
- AI 生成痕迹检测
- 提交历史分析
- 更新学生代码肖像

### ProfileService.java
- 学生肖像数据收集
- 知识点掌握度分析
- 薄弱点识别
- 学习行为分析
- 年度报告生成

### DashboardService.java
- 看板配置管理
- 自定义卡片查询解析
- 实时数据更新
- 数据可视化处理

### LLMService.java
- 统一的大模型调用接口
- 多模型支持（OpenAI/国产大模型）
- Prompt 模板管理
- Token 计数与控制
- 流式响应处理
