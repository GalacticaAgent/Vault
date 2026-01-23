# 前后端连通性分析报告

## 📡 连通状态：⚠️ 部分连通，存在路径不匹配问题

**检查时间**: 2026年1月22日  
**检查结果**: 前端和后端已配置代理，但API路径存在不匹配

---

## 🔗 连接配置

### 前端配置（Vite代理）

**文件**: `vault-frontend/vite.config.js`

```javascript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端地址
      changeOrigin: true
      // 不需要rewrite，因为后端的context-path就是/api
    }
  }
}
```

**前端请求配置**: `vault-frontend/src/api/request.js`
```javascript
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',  // 基础URL: /api
  timeout: 30000
})
```

### 后端配置（Spring Boot）

**文件**: `vault-backend/src/main/resources/application.yml`

```yaml
server:
  context-path: /api  # 应用上下文路径
```

### ✅ 代理连接正确

前端 `http://localhost:5173` 通过代理访问后端 `http://localhost:8080`：

```
前端请求: http://localhost:5173/api/xxx
  ↓ (Vite代理)
后端实际: http://localhost:8080/api/xxx
```

---

## ❌ API路径不匹配问题

### 问题1：问卷接口路径不匹配

**前端调用** (`questionnaire.js`):
```javascript
// ❌ 错误：使用了旧的通用路径
export function getQuestionnaireList(params) {
  return request({
    url: '/questionnaires',  // 实际请求: /api/questionnaires
    method: 'get'
  })
}

export function submitAnswers(id, data) {
  return request({
    url: `/questionnaires/${id}/submit`,  // 实际请求: /api/questionnaires/{id}/submit
    method: 'post'
  })
}
```

**后端实际路径** (已删除QuestionnaireController):
```java
// ✅ 教师端
@RequestMapping("/teacher/questionnaires")  // /api/teacher/questionnaires

// ✅ 学生端
@RequestMapping("/student/questionnaire")   // /api/student/questionnaire
```

**结果**: ❌ **404 Not Found** - 前端请求的 `/api/questionnaires` 不存在！

---

### 问题2：材料接口路径不匹配

**前端调用** (`material.js`):
```javascript
// ❌ 错误：使用了复数形式和旧的上传路径
export function uploadMaterial(data) {
  return request({
    url: '/materials',  // 实际请求: /api/materials
    method: 'post'
  })
}

export function getMaterialList(params) {
  return request({
    url: '/materials',  // 实际请求: /api/materials
    method: 'get'
  })
}

export function deleteMaterial(id) {
  return request({
    url: `/materials/${id}`,  // 实际请求: /api/materials/{id}
    method: 'delete'
  })
}
```

**后端实际路径**:
```java
// ✅ 公共查询（单数）
@RequestMapping("/material")  // /api/material (注意是单数!)

// ✅ 教师管理
@RequestMapping("/teacher/material")  // /api/teacher/material
```

**结果**: 
- ❌ `/api/materials` (复数) → 后端是 `/api/material` (单数)
- ❌ 上传和删除应该调用 `/api/teacher/material`

---

### 问题3：教师/学生接口部分正确

**前端调用** (`student.js`):
```javascript
// ✅ 正确：学生看板
export function getStudentDashboard() {
  return request({
    url: '/student/dashboard',  // /api/student/dashboard ✅
    method: 'get'
  })
}

// ✅ 正确：添加卡片
export function addDashboardCard(data) {
  return request({
    url: '/student/dashboard/card',  // /api/student/dashboard/card ✅
    method: 'post'
  })
}
```

**后端路径**:
```java
@RequestMapping("/student/dashboard")  // ✅ 匹配!
```

**前端调用** (`teacher.js`):
```javascript
// ✅ 正确：教师看板
export function getTeacherDashboard() {
  return request({
    url: '/teacher/dashboard',  // /api/teacher/dashboard ✅
    method: 'get'
  })
}
```

**后端路径**:
```java
@RequestMapping("/teacher/dashboard")  // ✅ 匹配!
```

---

## 📊 前后端路径对应表

| 功能 | 前端当前路径 | 后端实际路径 | 是否匹配 | 需要修改 |
|-----|------------|------------|---------|---------|
| **认证** |
| 登录 | `/auth/login` | `/api/auth/login` | ✅ | 无 |
| **学生端** |
| 学生看板 | `/student/dashboard` | `/api/student/dashboard` | ✅ | 无 |
| 添加卡片 | `/student/dashboard/card` | `/api/student/dashboard/card` | ✅ | 无 |
| **教师端** |
| 教师看板 | `/teacher/dashboard` | `/api/teacher/dashboard` | ✅ | 无 |
| **问卷（问题最大）** |
| 获取问卷列表 | `/questionnaires` | 不存在 | ❌ | 改为 `/teacher/questionnaires` 或 `/student/questionnaire/pending` |
| 获取问卷详情 | `/questionnaires/{id}` | 不存在 | ❌ | 改为 `/teacher/questionnaires/{id}` 或 `/student/questionnaire/{id}` |
| 提交答案 | `/questionnaires/{id}/submit` | 不存在 | ❌ | 改为 `/student/questionnaire/{id}/submit` |
| 生成问卷 | `/questionnaires/generate` | 不存在 | ❌ | 改为 `/teacher/questionnaires/generate` |
| **材料** |
| 上传材料 | `/materials` | 不存在 | ❌ | 改为 `/teacher/material/upload` |
| 获取列表 | `/materials` | `/api/material` | ⚠️ | 改为 `/material/list` (单数) |
| 获取详情 | `/materials/{id}` | `/api/material/{id}` | ⚠️ | 改为 `/material/{id}` (单数) |
| 删除材料 | `/materials/{id}` | 不存在 | ❌ | 改为 `/teacher/material/{id}` |

---

## 🔧 需要修复的前端文件

### 1. questionnaire.js - 全面修改

需要根据用户角色（教师/学生）调用不同的接口：

```javascript
// ❌ 错误的当前版本
export function getQuestionnaireList(params) {
  return request({
    url: '/questionnaires',
    method: 'get',
    params
  })
}

// ✅ 应该改为（教师端）
export function getTeacherQuestionnaireList(params) {
  return request({
    url: '/teacher/questionnaires',
    method: 'get',
    params
  })
}

// ✅ 应该改为（学生端）
export function getStudentPendingQuestionnaires(params) {
  return request({
    url: '/student/questionnaire/pending',
    method: 'get',
    params
  })
}

export function getStudentCompletedQuestionnaires(params) {
  return request({
    url: '/student/questionnaire/completed',
    method: 'get',
    params
  })
}

// ❌ 错误的提交接口
export function submitAnswers(id, data) {
  return request({
    url: `/questionnaires/${id}/submit`,
    method: 'post',
    data
  })
}

// ✅ 应该改为（学生端）
export function submitQuestionnaire(id, data) {
  return request({
    url: `/student/questionnaire/${id}/submit`,
    method: 'post',
    data
  })
}

// ❌ 错误的生成接口
export function generateQuestionnaire(data) {
  return request({
    url: '/questionnaires/generate',
    method: 'post',
    data
  })
}

// ✅ 应该改为（教师端）
export function generateQuestionnaire(data) {
  return request({
    url: '/teacher/questionnaires/generate',
    method: 'post',
    data
  })
}
```

---

### 2. material.js - 修改复数为单数，区分权限

```javascript
// ❌ 错误的上传接口
export function uploadMaterial(data) {
  return request({
    url: '/materials',  // 错误：应该是教师专用
    method: 'post',
    data
  })
}

// ✅ 应该改为（教师端）
export function uploadMaterial(data) {
  return request({
    url: '/teacher/material/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ❌ 错误的列表接口（复数）
export function getMaterialList(params) {
  return request({
    url: '/materials',  // 错误：应该是单数 /material
    method: 'get',
    params
  })
}

// ✅ 应该改为（公共查询）
export function getMaterialList(params) {
  return request({
    url: '/material/list',  // 改为单数
    method: 'get',
    params
  })
}

// ❌ 错误的详情接口（复数）
export function getMaterialDetail(id) {
  return request({
    url: `/materials/${id}`,  // 错误：应该是单数
    method: 'get'
  })
}

// ✅ 应该改为（公共查询）
export function getMaterialDetail(id) {
  return request({
    url: `/material/${id}`,  // 改为单数
    method: 'get'
  })
}

// ❌ 错误的删除接口
export function deleteMaterial(id) {
  return request({
    url: `/materials/${id}`,  // 错误：应该是教师专用
    method: 'delete'
  })
}

// ✅ 应该改为（教师端）
export function deleteMaterial(id) {
  return request({
    url: `/teacher/material/${id}`,
    method: 'delete'
  })
}
```

---

## 🎯 修复步骤

### Step 1: 修改 questionnaire.js

将通用问卷接口拆分为教师端和学生端：

```bash
修改文件: vault-frontend/src/api/questionnaire.js
```

### Step 2: 修改 material.js

修复复数/单数问题，区分教师和公共接口：

```bash
修改文件: vault-frontend/src/api/material.js
```

### Step 3: 更新前端组件调用

前端Vue组件需要根据用户角色调用不同的API：

```javascript
// 在教师页面
import { getTeacherQuestionnaireList, generateQuestionnaire } from '@/api/questionnaire'

// 在学生页面
import { getStudentPendingQuestionnaires, submitQuestionnaire } from '@/api/questionnaire'
```

---

## 📋 完整的前后端路径映射

### 后端可用接口（删除重复后）

```java
// 认证模块
POST   /api/auth/login
POST   /api/auth/register

// 聊天模块
GET    /api/chat/list
POST   /api/chat/send
WS     /api/chat/ws

// 学生模块
GET    /api/student/dashboard
POST   /api/student/dashboard/card
DELETE /api/student/dashboard/card/{id}
PUT    /api/student/dashboard/card/{id}
POST   /api/student/dashboard/card/{id}/refresh

GET    /api/student/questionnaire/pending
GET    /api/student/questionnaire/completed
GET    /api/student/questionnaire/{id}
POST   /api/student/questionnaire/{id}/submit
POST   /api/student/questionnaire/{id}/progress
GET    /api/student/questionnaire/{id}/progress
GET    /api/student/questionnaire/{id}/score
GET    /api/student/questionnaire/{id}/report
GET    /api/student/questionnaire/{id}/mistakes

// 教师模块
GET    /api/teacher/dashboard
POST   /api/teacher/dashboard/card
DELETE /api/teacher/dashboard/card/{id}
PUT    /api/teacher/dashboard/card/{id}
POST   /api/teacher/dashboard/card/{id}/refresh
GET    /api/teacher/dashboard/classes
GET    /api/teacher/dashboard/class/{id}/students
GET    /api/teacher/dashboard/class/{id}/statistics

POST   /api/teacher/questionnaires
GET    /api/teacher/questionnaires
GET    /api/teacher/questionnaires/{id}
PUT    /api/teacher/questionnaires
DELETE /api/teacher/questionnaires/{id}
POST   /api/teacher/questionnaires/{id}/publish
POST   /api/teacher/questionnaires/generate

POST   /api/teacher/material/upload
GET    /api/teacher/material/course/{id}
DELETE /api/teacher/material/{id}

// 材料公共查询
GET    /api/material/list
GET    /api/material/{id}
GET    /api/material/search

// Skills
GET    /api/skill/list
POST   /api/skill/execute
POST   /api/skill/reload
GET    /api/skill/search
```

---

## 🎉 结论

### 当前状态

- ✅ **代理配置正确** - Vite代理已配置，可以转发到后端
- ✅ **部分接口正确** - 学生看板、教师看板等接口路径正确
- ❌ **问卷接口全错** - 前端使用的 `/questionnaires` 已被删除
- ❌ **材料接口部分错** - 前端使用复数 `/materials`，后端是单数 `/material`

### 下一步行动

1. **立即修复** `questionnaire.js` - 拆分为教师和学生接口
2. **立即修复** `material.js` - 改为单数，区分权限
3. **测试验证** - 启动前后端，验证接口连通性

**是否需要我立即帮您修复这些前端API文件？**
