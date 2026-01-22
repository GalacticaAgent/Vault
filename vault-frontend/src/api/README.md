# API 接口层

## 职责
封装所有后端 API 接口调用，统一管理请求配置。

## 主要文件

### request.js
Axios 请求配置
- 基础 URL 配置
- 请求拦截器（添加 Token）
- 响应拦截器（错误处理）
- 超时设置

### user.js
用户相关接口
- `register(data)` - 用户注册
- `login(data)` - 用户登录
- `logout()` - 用户登出
- `getUserInfo()` - 获取用户信息
- `updateUserInfo(data)` - 更新用户信息
- `changePassword(data)` - 修改密码

### chat.js
对话相关接口
- `createSession(data)` - 创建会话
- `sendMessage(data)` - 发送消息
- `getSessionHistory(sessionId)` - 获取会话历史
- `getSessions()` - 获取会话列表
- `shareSession(sessionId)` - 分享会话
- `deleteSession(sessionId)` - 删除会话

### questionnaire.js
问卷相关接口
- `getQuestionnaireList(params)` - 获取问卷列表
- `getQuestionnaireDetail(id)` - 获取问卷详情
- `createQuestionnaire(data)` - 创建问卷
- `generateQuestionnaire(data)` - AI 生成问卷
- `submitAnswer(data)` - 提交答案
- `getScore(submissionId)` - 获取成绩

### code.js
代码相关接口
- `submitRepository(data)` - 提交代码仓库
- `analyzeCode(repoId)` - 触发代码分析
- `getAnalysisResult(taskId)` - 获取分析结果
- `getAIDetection(repoId)` - 获取 AI 检测结果

### profile.js
肖像相关接口
- `getStudentProfile(studentId)` - 获取学生肖像
- `getWeakPoints(studentId)` - 获取薄弱知识点
- `generateAnnualReport(studentId)` - 生成年度报告
- `analyzeClass(data)` - 班级肖像分析

### dashboard.js
看板相关接口
- `getDashboardConfig()` - 获取看板配置
- `saveDashboardConfig(data)` - 保存看板配置
- `addCard(data)` - 添加自定义卡片
- `refreshCardData(cardId)` - 刷新卡片数据
- `deleteCard(cardId)` - 删除卡片

## 使用示例
```javascript
import { login, getUserInfo } from '@/api/user'

// 登录
const result = await login({ username, password, role })

// 获取用户信息
const userInfo = await getUserInfo()
```
