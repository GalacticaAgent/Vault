# DeepSeek AI 集成配置指南

## 概述

本项目已集成 DeepSeek AI 接口，用于智能生成问卷题目。本文档说明如何配置和使用该功能。

## 配置步骤

### 1. 获取 DeepSeek API Key

访问 [DeepSeek 官网](https://www.deepseek.com/) 注册账号并获取 API Key。

### 2. 配置 API Key

有两种配置方式：

#### 方式一：环境变量（推荐用于生产环境）

```bash
export DEEPSEEK_API_KEY="sk-your-actual-api-key-here"
```

#### 方式二：本地配置文件（推荐用于开发环境）

1. 在 `vault-backend/src/main/resources/` 目录下创建 `application-local.yml` 文件

2. 添加以下内容：

```yaml
# 本地开发环境配置
deepseek:
  api-key: sk-your-actual-api-key-here
```

3. 启动应用时激活 local profile：

```bash
# 方式 A: 使用 Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local

# 方式 B: 在 IDEA 中
# Run -> Edit Configurations -> Active profiles: dev,local

# 方式 C: 使用 Java 启动参数
java -Dspring.profiles.active=dev,local -jar vault-backend-*.jar
```

### 3. 安全说明

⚠️ **重要：** `application-local.yml` 文件已被加入 `.gitignore`，不会提交到版本库。

**请勿将包含真实 API Key 的文件提交到 Git！**

## 配置参数说明

在 `application.yml` 中可以调整以下参数：

```yaml
deepseek:
  api-key: ${DEEPSEEK_API_KEY:}  # API密钥（从环境变量读取，默认为空）
  api-url: https://api.deepseek.com/chat/completions  # API地址（注意：无 /v1 前缀）
  model: deepseek-chat  # 模型名称
  max-tokens: 4000  # 最大生成token数
  temperature: 0.7  # 温度参数（0-1，越高越随机）
  timeout: 60000  # 请求超时时间（毫秒）
```

## 使用方法

### API 接口

**端点**: `POST /api/teacher/questionnaires/generate`

**请求示例**:

```json
{
  "title": "Java基础测试",
  "description": "基于知识点自动生成",
  "knowledgePoints": ["Java基础", "面向对象", "集合框架"],
  "difficulty": "MEDIUM",
  "questionCount": 10,
  "targetStudents": null
}
```

**请求头**:
```
Authorization: Bearer <teacher-jwt-token>
Content-Type: application/json
```

**响应**:
```json
{
  "code": 200,
  "message": "问卷生成成功",
  "data": {
    "id": 5,
    "title": "Java基础测试",
    "questions": [
      {
        "id": 20,
        "type": "SINGLE_CHOICE",
        "content": "Java是哪一年发布的？",
        "options": ["1990", "1995", "2000", "2005"],
        "correctAnswer": "1995",
        "score": 5.00
      }
      // ... 更多题目
    ]
  }
}
```

### 难度级别

- `EASY`: 简单（适合基础知识点）
- `MEDIUM`: 中等（适合综合应用）
- `HARD`: 困难（适合深入理解和拓展）

### 题目类型分配

AI 会按以下比例生成题目：

- 单选题（SINGLE_CHOICE）：40%
- 多选题（MULTIPLE_CHOICE）：30%
- 判断题（TRUE_FALSE）：20%
- 简答题（SHORT_ANSWER）：10%

### 分值规则

- 单选题：5分
- 多选题：10分
- 判断题：5分
- 简答题：15分

## 测试

### Postman 测试

1. 首先登录获取教师 Token：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testteacher001","password":"Teacher123"}'
```

2. 使用 Token 调用 AI 生成接口：

```bash
curl -X POST http://localhost:8080/api/teacher/questionnaires/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "测试AI生成",
    "description": "测试DeepSeek集成",
    "knowledgePoints": ["Java基础"],
    "difficulty": "EASY",
    "questionCount": 5
  }'
```

## 架构说明

### 主要组件

1. **DeepSeekConfig** (`config/DeepSeekConfig.java`)
   - 读取配置参数
   - 使用 `@ConfigurationProperties` 绑定配置

2. **DeepSeekService** (`service/ai/DeepSeekService.java`)
   - 封装 DeepSeek API 调用
   - 处理 HTTP 请求和响应

3. **QuestionGeneratorService** (`service/questionnaire/QuestionGeneratorService.java`)
   - 构建 AI prompt
   - 解析 AI 响应
   - 调用 QuestionnaireService 创建问卷

### 处理流程

```
用户请求
  ↓
TeacherQuestionnaireController
  ↓
QuestionGeneratorService
  ├─→ buildPrompt() - 构建提示词
  ├─→ DeepSeekService.chat() - 调用AI
  ├─→ parseAiResponse() - 解析响应
  └─→ QuestionnaireService.createQuestionnaire() - 创建问卷
```

## 故障排查

### 问题 1: API Key 未配置

**错误**: `AI 服务调用失败: 401 Unauthorized`

**解决**:
- 检查 `DEEPSEEK_API_KEY` 环境变量是否设置
- 检查 `application-local.yml` 中的 API 密钥是否正确
- 检查是否激活了 `local` profile（本地开发时）

### 问题 2: 网络连接失败

**错误**: `AI 服务调用失败: Connection timeout`

**解决**:
- 检查网络连接是否正常
- 确认 API URL 正确（应为 `https://api.deepseek.com/chat/completions`，无 `/v1` 前缀）
- 调整 `timeout` 参数（增大超时时间）
- 检查防火墙或代理设置

### 问题 3: 解析响应失败

**错误**: `解析 AI 响应失败，请重试`

**解决**:
- 查看日志中的 "AI 原始响应"
- 可能是 AI 返回格式不符合预期
- 尝试调整 prompt 或重试

## 成本控制

DeepSeek API 按 token 计费。建议：

1. 合理设置 `max-tokens` 参数
2. 控制 `questionCount` 数量
3. 监控 API 使用量
4. 为用户设置使用频率限制

## 安全最佳实践

✅ **正确做法**:
- 使用环境变量存储 API Key
- 将 `application-local.yml` 加入 `.gitignore`
- 生产环境使用密钥管理服务（如 AWS Secrets Manager）

❌ **错误做法**:
- 不要在代码中硬编码 API Key
- 不要提交包含真实密钥的配置文件
- 不要在日志中打印完整的 API Key

## 更多资源

- [DeepSeek 官方文档](https://platform.deepseek.com/docs)
- [DeepSeek API 参考](https://platform.deepseek.com/api-docs/)
- [Spring Boot 配置文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

---

**文档版本**: v1.0
**最后更新**: 2026-01-22
**维护者**: Vault 开发团队
