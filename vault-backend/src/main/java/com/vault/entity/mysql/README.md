# MySQL Entity

## 职责
定义 MySQL 数据库表对应的实体类。

## 主要实体

### User.java
用户实体
- 基本信息：用户名、密码、邮箱、角色
- 扩展信息：昵称、头像

### Student.java
学生实体
- 学号、专业、年级、班级
- 统计数据：提问数、分数

### Teacher.java
教师实体
- 工号、院系、职称
- 授课课程

### Chat.java
对话实体
- 对话信息、用户关联
- 分享功能

### Message.java
消息实体
- 消息角色、内容
- 附件、参考资料

### Questionnaire.java
问卷实体
- 问卷信息、创建者
- 状态管理

### Question.java
题目实体
- 题目类型、内容
- 选项、答案

### Answer.java
答案实体
- 答案内容
- 评分、批改

### Material.java
资料实体
- 资料信息
- 文件关联

### Skill.java
Skill 实体
- Skill 信息
- 文件关联

### CodeRepository.java
代码仓库实体
- 仓库信息
- 分析结果

