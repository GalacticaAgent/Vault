# DTO (Data Transfer Object) 层

## 职责
定义数据传输对象，用于接收前端请求参数，进行参数验证和数据封装。

## 命名规范
- 请求 DTO：`XxxDTO`
- 注册：`RegisterDTO`
- 登录：`LoginDTO`
- 创建：`CreateXxxDTO`
- 更新：`UpdateXxxDTO`
- 查询：`QueryXxxDTO`

## 主要 DTO

### RegisterDTO
- username (必填)
- password (必填，长度 6-20)
- email (必填，邮箱格式)
- role (必填：STUDENT/TEACHER)
- studentId (学生必填)
- realName

### LoginDTO
- username (必填)
- password (必填)
- role (必填：STUDENT/TEACHER)

### SendMessageDTO
- sessionId (必填)
- content (必填)
- attachments (可选)

### CreateQuestionnaireDTO
- title (必填)
- description
- questions (题目列表)
- targetStudents (目标学生列表)

### SubmitAnswerDTO
- questionnaireId (必填)
- answers (答案列表)
- saveOnly (是否仅保存)

## 验证注解
使用 Spring Validation 注解：
- @NotNull
- @NotBlank
- @Email
- @Size
- @Pattern
- @Valid
