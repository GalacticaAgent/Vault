# Store 状态管理层

## 职责
使用 Pinia 管理全局状态，包括用户信息、对话状态、看板配置等。

## 主要 Store

### modules/user.js
用户状态管理
- state
  - token: JWT Token
  - userInfo: 用户信息对象
  - role: 用户角色（STUDENT/TEACHER）
- getters
  - isLoggedIn: 是否已登录
  - isStudent: 是否为学生
  - isTeacher: 是否为教师
- actions
  - login(credentials): 登录
  - logout(): 登出
  - refreshToken(): 刷新 Token
  - updateUserInfo(data): 更新用户信息

### modules/chat.js
对话状态管理
- state
  - sessions: 会话列表
  - currentSessionId: 当前会话 ID
  - messages: 当前会话消息列表
  - isLoading: 加载状态
- actions
  - createSession(): 创建新会话
  - switchSession(sessionId): 切换会话
  - loadHistory(sessionId): 加载历史消息
  - sendMessage(message): 发送消息
  - clearSession(): 清空会话

### modules/dashboard.js
看板状态管理
- state
  - config: 看板配置
  - customCards: 自定义卡片列表
  - cardsData: 卡片数据缓存
- actions
  - loadConfig(): 加载看板配置
  - saveConfig(config): 保存配置
  - addCard(card): 添加卡片
  - removeCard(cardId): 删除卡片
  - refreshCard(cardId): 刷新卡片数据

### modules/questionnaire.js
问卷状态管理
- state
  - questionnaireList: 问卷列表
  - currentQuestionnaire: 当前问卷
  - answers: 答案缓存
- actions
  - loadQuestionnaireList(): 加载问卷列表
  - loadQuestionnaire(id): 加载问卷详情
  - saveAnswer(answer): 保存答案
  - submitQuestionnaire(): 提交问卷

## 使用示例
```javascript
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()

// 获取状态
const isLoggedIn = userStore.isLoggedIn
const userInfo = userStore.userInfo

// 调用 action
await userStore.login({ username, password, role })
```
