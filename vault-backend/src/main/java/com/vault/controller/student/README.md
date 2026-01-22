# Student Controller

## 职责
处理学生端相关的 HTTP 请求。

## 主要控制器

### StudentDashboardController.java
学生看板控制器
- GET /api/student/dashboard - 获取看板数据
- POST /api/student/dashboard/card - 添加自定义卡片
- DELETE /api/student/dashboard/card/{id} - 删除卡片

### StudentChatController.java
学生聊天控制器
- GET /api/student/chats - 获取对话列表
- POST /api/student/chats - 创建对话
- POST /api/student/chats/{id}/messages - 发送消息
- GET /api/student/chats/{id}/messages - 获取消息历史

### StudentQuestionnaireController.java
学生问卷控制器
- GET /api/student/questionnaires - 获取问卷列表
- GET /api/student/questionnaires/{id} - 获取问卷详情
- POST /api/student/questionnaires/{id}/submit - 提交答案

