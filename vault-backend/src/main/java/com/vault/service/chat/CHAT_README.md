# Chat Service README

## 智能问答系统说明

本模块实现了 Vault 的核心智能问答功能，基于 RAG（检索增强生成）和 Skills 系统。

## 目录结构

```
service/chat/
├── ChatService.java           # 核心聊天服务
├── RAGService.java           # RAG检索服务
├── FileService.java          # 文件处理服务
├── ChatShareService.java    # 分享服务
└── RAGSearchResult.java     # RAG结果对象
```

## 核心功能

### 1. 智能问答
- 支持文字问答
- 基于RAG的资料检索
- 引用资料来源
- 自动调用Skills

### 2. 文件处理
- 支持上传代码文件（Java, Python, C++等）
- 支持上传PDF文档
- 自动提取文本内容
- 代码语言检测

### 3. Skills系统
- 参考 chat-skills 项目实现
- 支持 Markdown 格式的 Skill 定义
- 自动路由到合适的 Skill
- 工作流执行引擎

### 4. WebSocket通信
- 实时消息推送
- 流式响应（逐字显示）
- 心跳保持连接

### 5. 对话分享
- 生成分享链接
- 支持密码保护
- 设置过期时间

## API接口

### REST API

- `POST /api/chat/send` - 发送消息
- `GET /api/chat/list` - 获取会话列表
- `GET /api/chat/{chatId}` - 获取会话详情
- `DELETE /api/chat/{chatId}` - 删除会话
- `POST /api/chat/upload` - 上传文件
- `POST /api/chat/share` - 创建分享
- `GET /api/chat/share/{shareId}` - 查看分享

### WebSocket

- 连接地址：`/ws/chat`
- 发送消息：`/app/chat/send`
- 接收消息：`/user/{userId}/queue/messages`

## 配置说明

在 `application.yml` 中配置：

```yaml
vault:
  # AI配置
  ai:
    api-key: your-openai-api-key
    api-url: https://api.openai.com/v1/chat/completions
    default-model: gpt-4
  
  # Skill配置
  skill:
    directory: skills
  
  # 文件配置
  file:
    upload-dir: uploads
    max-size: 10485760  # 10MB
  
  # Milvus配置
  milvus:
    host: localhost
    port: 19530
  
  # MinIO配置
  minio:
    endpoint: http://localhost:9000
    bucket: vault-files
```

## 数据库表

需要创建以下表：

```sql
-- 聊天会话表
CREATE TABLE chats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    status VARCHAR(20),
    last_message TEXT,
    last_message_time DATETIME,
    message_count INT DEFAULT 0,
    pinned BOOLEAN DEFAULT FALSE,
    create_time DATETIME,
    update_time DATETIME,
    deleted INT DEFAULT 0
);

-- 消息表
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT NOT NULL,
    role VARCHAR(20),
    content LONGTEXT,
    file_ids VARCHAR(500),
    material_ids VARCHAR(500),
    references LONGTEXT,
    skill_used VARCHAR(100),
    token_usage VARCHAR(200),
    knowledge_points VARCHAR(500),
    status VARCHAR(20),
    error_message TEXT,
    create_time DATETIME,
    deleted INT DEFAULT 0
);

-- 文件表
CREATE TABLE chat_files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chat_id BIGINT,
    message_id BIGINT,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(200),
    file_type VARCHAR(20),
    file_size BIGINT,
    file_path VARCHAR(500),
    file_url VARCHAR(500),
    extracted_content LONGTEXT,
    language VARCHAR(50),
    status VARCHAR(20),
    create_time DATETIME,
    deleted INT DEFAULT 0
);

-- 分享表
CREATE TABLE chat_shares (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    share_id VARCHAR(50) UNIQUE,
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    description TEXT,
    view_count INT DEFAULT 0,
    expire_time DATETIME,
    require_password BOOLEAN DEFAULT FALSE,
    password VARCHAR(100),
    status VARCHAR(20),
    create_time DATETIME,
    deleted INT DEFAULT 0
);
```

## 技术栈

- Spring Boot 2.x
- MyBatis Plus
- WebSocket (STOMP)
- OpenAI API
- Milvus (向量数据库)
- Neo4j (知识图谱)
- MinIO (对象存储)
- PDFBox (PDF解析)
