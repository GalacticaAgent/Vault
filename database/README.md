# Vault 数据库文档

## 📚 概述

Vault 数据库采用 MySQL 8.0+ 设计,为 CS 教学系统提供完整的数据存储和管理方案。数据库包含用户管理、问卷系统、聊天记录、资料管理、代码分析等核心功能模块。

## 🗂️ SQL 文件说明

| 文件名 | 说明 | 执行顺序 |
|--------|------|----------|
| `00_install_all.sql` | **主安装脚本**，自动执行所有脚本 | 1 |
| `01_create_database.sql` | 创建数据库和基本配置 | 2 |
| `02_create_tables.sql` | 创建所有业务表（20张表） | 3 |
| `03_create_views.sql` | 创建视图（16个视图） | 4 |
| `04_create_procedures.sql` | 创建存储过程（13个） | 5 |
| `05_create_triggers.sql` | 创建触发器（12个） | 6 |
| `06_create_functions.sql` | 创建函数（14个） | 7 |
| `07_create_indexes.sql` | 创建优化索引 | 8 |
| `08_insert_initial_data.sql` | 插入初始测试数据 | 9 |

## 🚀 快速安装

### 方法一：一键安装（推荐）

```bash
cd database
mysql -u root -p < 00_install_all.sql
```

### 方法二：逐步执行

```bash
mysql -u root -p < 01_create_database.sql
mysql -u root -p < 02_create_tables.sql
# ... 依次执行其他文件
```

## 📊 数据库结构

### 核心表（20张）

1. **用户相关** (3张): `users`, `students`, `teachers`
2. **聊天相关** (2张): `chats`, `messages`
3. **问卷相关** (4张): `questionnaires`, `questions`, `answers`, `questionnaire_submissions`
4. **资料相关** (1张): `materials`
5. **代码分析** (2张): `code_repositories`, `code_commits`
6. **Skills** (1张): `skills`
7. **看板** (1张): `dashboard_cards`
8. **系统表** (5张): `files`, `operation_logs`, `error_logs`, `notifications`, `db_version`
9. **通知** (1张): `notifications`

### 视图（16个）

- `v_user_full_info` - 完整用户信息
- `v_student_ranking` - 学生排行榜
- `v_questionnaire_stats` - 问卷统计
- `v_student_code_quality` - 代码质量
- `v_student_knowledge_mastery` - 知识点掌握
- 等...

### 存储过程（13个）

- `sp_create_student` - 创建学生
- `sp_submit_questionnaire` - 提交问卷
- `sp_get_student_weak_points` - 获取薄弱知识点
- `sp_get_class_stats` - 班级统计
- 等...

### 触发器（12个）

- `tr_auto_grade_answer` - 自动评分
- `tr_increment_question_count` - 提问计数
- `tr_material_upload_notification` - 资料上传通知
- 等...

### 函数（14个）

- `fn_calculate_pass_rate` - 计算通过率
- `fn_get_student_class_rank` - 获取排名
- `fn_can_access_questionnaire` - 权限检查
- 等...

## 🔐 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 教师 | teacher001 | teacher123 |
| 学生 | student001 | student123 |

⚠️ **生产环境请立即修改默认密码！**

## 📝 配置说明

### MySQL 配置

```ini
[mysqld]
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
innodb_buffer_pool_size = 1G
max_connections = 200
default-time-zone = '+08:00'
```

### Spring Boot 配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vault?useUnicode=true&characterEncoding=utf8mb4
    username: your_username
    password: your_password
```

## 🔍 常用操作

### 查询示例

```sql
-- 查看学生排行榜
SELECT * FROM v_student_ranking LIMIT 10;

-- 获取薄弱知识点
CALL sp_get_student_weak_points(1, 5);

-- 计算问卷通过率
SELECT fn_calculate_pass_rate(1);
```

### 维护操作

```sql
-- 备份数据库
mysqldump -u root -p vault > backup.sql

-- 清理过期日志（30天）
CALL sp_clean_expired_data(30);

-- 更新学生统计
CALL sp_update_all_student_stats();
```

## 📈 性能优化

- ✅ 完善的索引设计（主键、唯一、外键、复合索引）
- ✅ 视图简化复杂查询
- ✅ 存储过程减少网络开销
- ✅ 触发器实现自动化
- ✅ 建议使用 Redis 缓存热点数据

## ⚠️ 注意事项

1. 生产环境必须修改默认密码
2. 配置定期备份策略
3. 启用慢查询日志监控性能
4. 定期执行表优化操作
5. 密码使用 BCrypt 加密存储

## 📧 技术支持

详细文档请查看：
- `/test/docs/design/数据库设计文档.md`
- `/test/docs/design/整体架构文档.md`

---
**Version**: 1.0 | **Date**: 2026-01-21
