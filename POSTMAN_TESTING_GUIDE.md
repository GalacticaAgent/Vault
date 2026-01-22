# Vault API Postman 测试指南

## 环境配置

### 1. 基础配置

**Base URL**: `http://localhost:8080/api`

**Headers（全局配置）**:
- `Content-Type`: `application/json`
- `Authorization`: `Bearer {token}` (登录后获取)

### 2. Postman 环境变量设置

创建环境变量以便复用：

| 变量名 | 初始值 | 说明 |
|--------|--------|------|
| `base_url` | `http://localhost:8080/api` | API 基础地址 |
| `token` | (空) | 登录后自动设置 |
| `user_id` | (空) | 登录后自动设置 |

## 测试流程

### 步骤 1: 测试 API 连通性

**请求**: GET `/auth/test`

```
GET {{base_url}}/auth/test
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": "API正常运行"
}
```

---

### 步骤 2: 用户登录

**请求**: POST `/auth/login`

```
POST {{base_url}}/auth/login
Content-Type: application/json

{
  "username": "student001",
  "password": "student123"
}
```

**测试账号**:
- 学生: `student001` / `student123`
- 教师: `teacher001` / `teacher123`
- 管理员: `admin` / `admin123`

**预期响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "student001",
    "role": "STUDENT"
  }
}
```

**Postman 自动化脚本**（Tests 标签页）:
```javascript
// 保存 token 到环境变量
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.token);
    pm.environment.set("user_id", jsonData.data.userId);
    console.log("Token saved:", jsonData.data.token);
}
```

---

### 步骤 3: 用户注册（可选）

**请求**: POST `/auth/register`

```
POST {{base_url}}/auth/register
Content-Type: application/json

{
  "username": "newstudent",
  "password": "password123",
  "email": "newstudent@example.com",
  "role": "STUDENT"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 2,
    "username": "newstudent",
    "role": "STUDENT"
  }
}
```

---

### 步骤 4: 获取问卷列表

**请求**: GET `/student/questionnaire/list`

```
GET {{base_url}}/student/questionnaire/list
Authorization: Bearer {{token}}
```

**可选参数**:
- `status=pending` - 获取待完成问卷
- `status=completed` - 获取已完成问卷
- 不传参数 - 获取全部问卷

**示例**:
```
GET {{base_url}}/student/questionnaire/list?status=pending
Authorization: Bearer {{token}}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "Java 基础知识测试",
      "description": "测试 Java 基础知识掌握情况",
      "totalQuestions": 10,
      "duration": 30,
      "status": "pending",
      "deadline": "2026-02-01T23:59:59"
    }
  ]
}
```

---

### 步骤 5: 获取问卷详情

**请求**: GET `/student/questionnaire/{id}`

```
GET {{base_url}}/student/questionnaire/1
Authorization: Bearer {{token}}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "Java 基础知识测试",
    "description": "测试 Java 基础知识掌握情况",
    "duration": 30,
    "questions": [
      {
        "id": 1,
        "content": "Java 是什么类型的语言？",
        "type": "SINGLE_CHOICE",
        "options": [
          {
            "id": 1,
            "content": "编译型语言",
            "label": "A"
          },
          {
            "id": 2,
            "content": "解释型语言",
            "label": "B"
          },
          {
            "id": 3,
            "content": "混合型语言",
            "label": "C"
          }
        ],
        "score": 10
      }
    ]
  }
}
```

---

### 步骤 6: 提交问卷答案

**请求**: POST `/student/questionnaire/submit`

```
POST {{base_url}}/student/questionnaire/submit
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "questionnaireId": 1,
  "timeSpent": 135,
  "answers": [
    {
      "questionId": 1,
      "answerContent": "D"
    },
    {
      "questionId": 2,
      "answerContent": "A,B"
    },
    {
      "questionId": 3,
      "answerContent": "Java 是一种面向对象的编程语言..."
    }
  ]
}
```

**字段说明**:
- `questionnaireId`: 问卷 ID
- `timeSpent`: 总答题用时（秒）
- `answers`: 答案数组
  - `questionId`: 问题 ID
  - `answerContent`: 答案内容
    - 单选题/判断题：选项字母，如 "A"、"D"、"T"、"F"
    - 多选题：逗号分隔的选项字母，如 "A,B,C"
    - 简答题：文本答案
  - `timeSpent`: 单题用时（秒，可选）

**预期响应**:
```json
{
  "code": 200,
  "message": "提交成功",
  "data": null
}
```

---

### 步骤 7: 获取问卷结果

**请求**: GET `/student/questionnaire/{id}/result`

```
GET {{base_url}}/student/questionnaire/1/result
Authorization: Bearer {{token}}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "questionnaireId": 1,
    "title": "Java 基础知识测试",
    "totalScore": 100,
    "obtainedScore": 85,
    "correctCount": 8,
    "wrongCount": 2,
    "submittedAt": "2026-01-22T10:30:00",
    "answers": [
      {
        "questionId": 1,
        "content": "Java 是什么类型的语言？",
        "yourAnswer": "混合型语言",
        "correctAnswer": "混合型语言",
        "isCorrect": true,
        "score": 10
      }
    ]
  }
}
```

---

## 常见错误处理

### 401 Unauthorized

**错误响应**:
```json
{
  "code": 401,
  "message": "未授权，请先登录"
}
```

**解决方法**:
1. 确认已执行登录请求
2. 检查 `Authorization` header 是否正确设置
3. 确认 token 格式为 `Bearer {token}`
4. Token 可能已过期（24小时），需重新登录

### 403 Forbidden

**错误响应**:
```json
{
  "code": 403,
  "message": "权限不足"
}
```

**解决方法**:
- 确认使用正确角色的账号（学生/教师/管理员）
- 学生账号只能访问 `/student/*` 接口

### 404 Not Found

**错误响应**:
```json
{
  "code": 404,
  "message": "资源不存在"
}
```

**解决方法**:
- 检查 URL 路径是否正确
- 确认资源 ID 是否存在

### 400 Bad Request

**错误响应**:
```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": {
    "username": "用户名不能为空",
    "password": "密码不能为空"
  }
}
```

**解决方法**:
- 检查请求体 JSON 格式是否正确
- 确认必填字段已填写
- 检查字段类型是否匹配

---

## Postman Collection 导入

### 创建 Collection

1. 打开 Postman
2. 点击 "New" → "Collection"
3. 命名为 "Vault API"
4. 在 "Variables" 标签页添加环境变量

### 添加请求

按照上述步骤依次创建以下请求：

```
Vault API/
├── Auth/
│   ├── Test API
│   ├── Login
│   └── Register
└── Student Questionnaire/
    ├── Get Questionnaire List
    ├── Get Questionnaire Detail
    ├── Submit Answers
    └── Get Result
```

### 设置 Collection 级别的 Authorization

1. 选择 "Vault API" Collection
2. 进入 "Authorization" 标签页
3. Type 选择 "Bearer Token"
4. Token 填写 `{{token}}`
5. 所有子请求将自动继承此配置

---

## 测试建议

### 1. 按顺序测试

建议按以下顺序执行测试：
1. Test API（验证连通性）
2. Login（获取 token）
3. Get Questionnaire List（验证认证）
4. Get Questionnaire Detail（获取问卷内容）
5. Submit Answers（提交答案）
6. Get Result（查看结果）

### 2. 使用 Collection Runner

1. 点击 Collection 右侧的 "..."
2. 选择 "Run collection"
3. 选择要运行的请求
4. 点击 "Run Vault API"
5. 查看批量测试结果

### 3. 保存测试用例

在每个请求的 "Tests" 标签页添加断言：

```javascript
// 验证状态码
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 验证响应结构
pm.test("Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
    pm.expect(jsonData).to.have.property('data');
});

// 验证业务逻辑
pm.test("Login successful", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.eql(200);
    pm.expect(jsonData.data.token).to.be.a('string');
});
```

---

## API 文档访问

除了 Postman，您还可以通过以下方式查看 API 文档：

- **Knife4j**: http://localhost:8080/api/doc.html
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

这些文档提供了交互式 API 测试界面，可以直接在浏览器中测试 API。

---

## 附录：完整测试数据示例

### 登录请求示例
```json
{
  "username": "student001",
  "password": "student123"
}
```

### 提交答案请求示例
```json
{
  "questionnaireId": 1,
  "timeSpent": 135,
  "answers": [
    {
      "questionId": 1,
      "answerContent": "D"
    },
    {
      "questionId": 2,
      "answerContent": "A,B"
    },
    {
      "questionId": 3,
      "answerContent": "Java 是一种面向对象的编程语言，具有跨平台、安全性高、性能优秀等特点。"
    }
  ]
}
```

---

**文档版本**: 1.0
**更新日期**: 2026-01-22
**适用版本**: Vault Backend v1.0
