# Entity 层

## 职责
定义数据库表对应的实体类，使用 JPA/MyBatis-Plus 注解映射。

## 主要实体

### User.java
用户实体，对应 `user` 表
- 基本信息：用户名、密码、邮箱、手机号
- 角色信息：STUDENT/TEACHER
- 扩展信息：学号、真实姓名、班级

### KGNode.java
知识图谱节点实体，对应 `kg_node` 表
- 节点类型：MATERIAL/PERSON/CODE_REPO/QUESTION/ANSWER/KNOWLEDGE_POINT
- 内容：标题、内容、元数据
- 向量：embedding 向量数据

### KGRelation.java
知识图谱关系实体，对应 `kg_relation` 表
- 关系类型：BELONGS_TO/REFERENCES/MASTERS/WEAK_IN/RELATED_TO
- 节点关联：源节点 ID、目标节点 ID
- 权重：关系强度

### ChatSession.java
对话会话实体，对应 `chat_session` 表
- 会话信息：标题、上下文
- 用户关联：用户 ID
- 分享：分享令牌

### ChatMessage.java
对话消息实体，对应 `chat_message` 表
- 消息角色：USER/ASSISTANT/SYSTEM
- 消息内容：文本、附件
- 关联：会话 ID

### Questionnaire.java
问卷实体，对应 `questionnaire` 表
- 问卷信息：标题、描述、策略
- 状态：发布状态
- 创建者：教师 ID

### Question.java
题目实体，对应 `question` 表
- 题目类型：SINGLE_CHOICE/MULTIPLE_CHOICE/TRUE_FALSE/SHORT_ANSWER/CODING
- 题目内容：题干、选项、答案
- 元数据：分数、知识点标签

### StudentProfile.java
学生肖像实体，对应 `student_profile` 表
- 知识掌握：知识点掌握度数据
- 学习行为：活跃度、学习风格
- 代码能力：代码质量评分
