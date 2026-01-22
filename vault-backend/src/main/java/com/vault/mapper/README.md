# Mapper 层

## 职责
数据访问层，使用 MyBatis-Plus 进行数据库操作，定义 SQL 映射。

## 主要 Mapper

### UserMapper.java
- 用户 CRUD 操作
- 根据用户名/学号查询
- 用户列表分页查询

### KGNodeMapper.java
- 知识图谱节点 CRUD
- 节点类型筛选
- 全文检索支持

### KGRelationMapper.java
- 知识图谱关系 CRUD
- 关系查询（源节点/目标节点）
- 关系类型筛选

### ChatSessionMapper.java
- 会话 CRUD 操作
- 用户会话列表查询
- 分享链接查询

### ChatMessageMapper.java
- 消息 CRUD 操作
- 会话历史查询
- 消息分页加载

### QuestionnaireMapper.java
- 问卷 CRUD 操作
- 问卷列表查询（按状态/教师）
- 问卷发布管理

### QuestionMapper.java
- 题目 CRUD 操作
- 问卷题目查询
- 题目排序管理

### ProfileMapper.java
- 学生肖像 CRUD
- 知识追踪数据管理
- 肖像统计查询

## MyBatis-Plus 增强
- 自动分页
- 逻辑删除
- 乐观锁
- 自动填充（创建时间/更新时间）
