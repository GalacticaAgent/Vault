# VO (View Object) 层

## 职责
定义视图对象，用于返回给前端的数据格式，隐藏敏感信息，进行数据转换。

## 命名规范
- 视图对象：`XxxVO`
- 用户：`UserVO`
- Token：`TokenVO`
- 列表项：`XxxListVO`

## 主要 VO

### UserVO
返回用户信息（不包含密码）
- id
- username
- email
- phone
- role
- studentId
- realName
- className

### TokenVO
登录成功返回的 Token 信息
- token (JWT Token)
- tokenType ("Bearer")
- expiresIn (过期时间，秒)
- userInfo (用户基本信息)

### MessageVO
对话消息视图
- id
- sessionId
- role (USER/ASSISTANT)
- content
- attachments
- createdAt
- references (参考资料)

### QuestionnaireVO
问卷详情视图
- id
- title
- description
- questions (题目列表)
- status (状态)
- createdAt
- totalScore (总分)

### ProfileVO
学生肖像视图
- studentId
- studentName
- knowledgeMastery (知识掌握度 Map)
- weakPoints (薄弱知识点列表)
- learningStyle (学习风格)
- codeQuality (代码质量评分)
- activityScore (活跃度)

## 数据脱敏
- 密码字段不返回
- 敏感手机号/邮箱部分隐藏
- Token 等敏感信息加密传输
