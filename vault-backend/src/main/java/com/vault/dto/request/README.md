# Request DTO

## 职责
定义请求数据传输对象。

## 主要 DTO

### LoginRequest.java
登录请求
- 用户名
- 密码

### SendMessageRequest.java
发送消息请求
- 对话 ID
- 消息内容
- 附件

### CreateCardRequest.java
创建卡片请求
- 卡片标题
- 查询描述
- 刷新频率

### GenerateQuestionnaireRequest.java
生成问卷请求
- 问卷策略
- 目标学生
- 题目数量

### SubmitAnswersRequest.java
提交答案请求
- 问卷 ID
- 答案列表

