# Teacher Controller

## 职责
处理教师端相关的 HTTP 请求。

## 主要控制器

### TeacherDashboardController.java
教师看板控制器
- GET /api/teacher/dashboard - 获取看板数据
- POST /api/teacher/dashboard/card - 添加自定义卡片

### TeacherChatController.java
教师聊天控制器
- GET /api/teacher/chats - 获取对话列表
- POST /api/teacher/chats - 创建对话
- POST /api/teacher/chats/{id}/messages - 发送消息

### QuestionnaireGeneratorController.java
问卷生成控制器
- POST /api/teacher/questionnaires/generate - 生成问卷
- GET /api/teacher/questionnaires - 获取问卷列表
- GET /api/teacher/questionnaires/{id} - 获取问卷详情

### MaterialController.java
资料管理控制器
- POST /api/teacher/materials - 上传资料
- GET /api/teacher/materials - 获取资料列表
- DELETE /api/teacher/materials/{id} - 删除资料

### SkillController.java
Skill 管理控制器
- POST /api/teacher/skills - 上传 Skill
- GET /api/teacher/skills - 获取 Skill 列表
- DELETE /api/teacher/skills/{id} - 删除 Skill

