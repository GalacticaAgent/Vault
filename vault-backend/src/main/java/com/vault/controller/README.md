# Controller 层

## 职责
处理 HTTP 请求，定义 RESTful API 接口，参数验证，调用 Service 层业务逻辑。

## 主要控制器

### UserController.java
- 用户注册、登录、登出
- 用户信息查询与更新
- 密码修改

### KnowledgeGraphController.java
- 知识图谱节点管理（资料、个人、代码等）
- 知识图谱关系管理
- 子图查询与提取

### ChatController.java
- 创建对话会话
- 发送消息并获取 AI 回复
- 流式响应（SSE）
- 会话历史管理
- 对话分享

### QuestionnaireController.java
- 问卷创建（手动/AI 生成）
- 问卷分发与管理
- 答题提交
- 成绩查询

### CodeAnalysisController.java
- 代码仓库提交
- 触发代码分析
- 获取分析结果
- AI 代码检测

### ProfileController.java
- 学生肖像查询
- 薄弱知识点分析
- 年度报告生成
- 班级肖像分析

### DashboardController.java
- 看板配置管理
- 自定义卡片添加
- 卡片数据刷新

## 统一响应格式
```java
Result<T> {
    code: 200/400/500
    message: "提示信息"
    data: T
    timestamp: Long
}
```
