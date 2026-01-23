# Milvus 向量数据库安装与配置指南

## 什么是 Milvus？

Milvus 是一个开源的向量数据库，用于存储和检索高维向量数据。在 Vault 系统中，它用于存储教材文本的向量表示，实现语义相似度搜索（RAG功能）。

## 方式一：使用 Docker 安装（推荐）

### 1. 安装 Docker Desktop for Windows

1. 下载 Docker Desktop: https://www.docker.com/products/docker-desktop/
2. 安装并启动 Docker Desktop
3. 确保 WSL 2 已启用

### 2. 启动 Milvus Standalone（单机版）

使用 docker-compose 方式：

```bash
# 进入项目目录
cd C:\Users\Lenovo\Desktop\test\Vault

# 创建 milvus 目录
mkdir milvus
cd milvus

# 下载 docker-compose.yml
curl -o docker-compose.yml https://github.com/milvus-io/milvus/releases/download/v2.3.4/milvus-standalone-docker-compose.yml
```

或者手动创建 `docker-compose.yml`：

```yaml
version: '3.5'

services:
  etcd:
    container_name: milvus-etcd
    image: quay.io/coreos/etcd:v3.5.5
    environment:
      - ETCD_AUTO_COMPACTION_MODE=revision
      - ETCD_AUTO_COMPACTION_RETENTION=1000
      - ETCD_QUOTA_BACKEND_BYTES=4294967296
      - ETCD_SNAPSHOT_COUNT=50000
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/etcd:/etcd
    command: etcd -advertise-client-urls=http://127.0.0.1:2379 -listen-client-urls http://0.0.0.0:2379 --data-dir /etcd
    healthcheck:
      test: ["CMD", "etcdctl", "endpoint", "health"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio:
    container_name: milvus-minio
    image: minio/minio:RELEASE.2023-03-20T20-16-18Z
    environment:
      MINIO_ACCESS_KEY: minioadmin
      MINIO_SECRET_KEY: minioadmin
    ports:
      - "9001:9001"
      - "9000:9000"
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/minio:/minio_data
    command: minio server /minio_data --console-address ":9001"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  standalone:
    container_name: milvus-standalone
    image: milvusdb/milvus:v2.3.4
    command: ["milvus", "run", "standalone"]
    environment:
      ETCD_ENDPOINTS: etcd:2379
      MINIO_ADDRESS: minio:9000
    volumes:
      - ${DOCKER_VOLUME_DIRECTORY:-.}/volumes/milvus:/var/lib/milvus
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9091/healthz"]
      interval: 30s
      start_period: 90s
      timeout: 20s
      retries: 3
    ports:
      - "19530:19530"
      - "9091:9091"
    depends_on:
      - "etcd"
      - "minio"

networks:
  default:
    name: milvus
```

### 3. 启动服务

```bash
docker-compose up -d
```

### 4. 验证安装

```bash
# 检查容器状态
docker-compose ps

# 应该看到3个容器都在运行：
# - milvus-standalone
# - milvus-etcd
# - milvus-minio
```

访问 Milvus 管理界面（可选）：
- Attu (Milvus GUI): https://github.com/zilliztech/attu
- 或使用命令行工具验证连接

### 5. 停止/重启服务

```bash
# 停止
docker-compose down

# 重启
docker-compose restart

# 停止并删除数据
docker-compose down -v
```

## 方式二：Windows 本地安装（不推荐）

Milvus 官方不提供 Windows 原生安装包，建议使用 Docker。

## 配置 Vault 连接 Milvus

已在 `vault-backend/src/main/resources/application.yml` 配置：

```yaml
# Milvus 配置
milvus:
  host: localhost
  port: 19530
```

## 安装 Embedding 服务

Vault 使用文本 Embedding 将文本转换为向量。有以下选项：

### 选项1：使用 OpenAI API（推荐）

在 `application.yml` 添加：

```yaml
vault:
  embedding:
    provider: openai
    api-key: your-openai-api-key
    model: text-embedding-ada-002
    api-url: https://api.openai.com/v1/embeddings
```

### 选项2：使用本地 Embedding 模型

使用 Sentence Transformers 本地模型（需要 Python 服务）：

```bash
# 安装依赖
pip install sentence-transformers flask

# 创建简单的 Embedding 服务
# embedding_service.py
```

### 选项3：使用 DeepSeek 或其他国内服务

某些国内大模型提供 Embedding API。

## 初始化 Milvus Collection

系统启动时会自动创建 Collection（在 `MilvusConfig.java` 中实现）。

Collection 结构：
- **collection_name**: `vault_materials`
- **向量维度**: 1536 (OpenAI) 或 768 (其他模型)
- **字段**:
  - `id`: 主键
  - `material_id`: 教材ID
  - `material_name`: 教材名称
  - `chapter`: 章节
  - `content`: 文本内容
  - `embedding`: 向量字段
  - `position`: 文本位置

## 验证 RAG 功能

### 1. 启动所有服务

```bash
# 启动 Milvus
cd milvus
docker-compose up -d

# 启动 MySQL, Redis
# ...

# 启动后端
cd vault-backend
mvn spring-boot:run
```

### 2. 上传教材

使用教师账号上传 PDF 教材，系统会自动：
1. 提取文本内容
2. 分块（每500字符）
3. 调用 Embedding API 生成向量
4. 存储到 Milvus

### 3. 测试智能问答

使用学生账号提问，系统会：
1. 将问题转换为向量
2. 在 Milvus 中检索相似文本块
3. 将检索结果作为上下文发送给 LLM
4. 返回带有引用来源的答案

## 常见问题

### Q: Milvus 启动失败？
A: 确保 Docker Desktop 正在运行，且端口 19530 未被占用。

### Q: 向量检索没有结果？
A: 检查是否已上传教材并成功建立索引。

### Q: Embedding API 调用失败？
A: 检查 API key 是否正确，网络是否可访问。

### Q: 如何查看 Milvus 中的数据？
A: 使用 Attu GUI 工具或通过代码查询。

## 资源链接

- Milvus 官方文档: https://milvus.io/docs
- Milvus GitHub: https://github.com/milvus-io/milvus
- Attu (GUI): https://github.com/zilliztech/attu
- OpenAI Embeddings: https://platform.openai.com/docs/guides/embeddings

## 下一步

完成 Milvus 安装后，系统将自动启用完整的 RAG 功能。继续阅读 `DEPLOYMENT_GUIDE.md` 了解完整部署流程。
