# Vault 数据库使用指南

## 📋 关于两个安装脚本

数据库文件夹中有两个安装脚本，**作用不同**：

| 脚本文件 | 用途 | 包含内容 | 适用场景 |
|---------|------|---------|---------|
| `00_install_all.sql` | 开发环境 | ✅ 数据库结构<br>✅ 测试数据<br>✅ 测试账号 | 本地开发、测试 |
| `00_install_structure_only.sql` | 生产环境 | ✅ 数据库结构<br>❌ 无测试数据<br>❌ 无测试账号 | 生产部署、多人共享 |

**当前已安装**: `00_install_structure_only.sql` ✅

---

## 🚀 完整使用步骤（供其他开发者）

### 方式一：直接使用已建好的数据库（推荐）

#### 第一步：连接到MySQL服务器

```bash
# Windows PowerShell
mysql -u root -p -h localhost -P 3306

# 或使用图形化工具（如Navicat、MySQL Workbench）
# Host: localhost
# Port: 3306
# Database: vault
```

#### 第二步：验证数据库存在

```sql
-- 查看所有数据库
SHOW DATABASES;

-- 切换到vault数据库
USE vault;

-- 查看所有表
SHOW TABLES;
```

你应该看到以下19个表和16个视图：

**数据表（19张）**：
- users
- students
- teachers
- chats
- messages
- questionnaires
- questions
- answers
- questionnaire_submissions
- materials
- code_repositories
- code_commits
- skills
- dashboard_cards
- files
- operation_logs
- error_logs
- notifications
- db_version

**视图（16个）**：
- v_user_full_info
- v_student_ranking
- v_questionnaire_stats
- v_question_accuracy
- v_student_chat_summary
- v_student_code_quality
- v_material_download_ranking
- v_student_knowledge_mastery
- v_teacher_performance
- v_class_performance
- v_student_activity
- v_question_difficulty
- v_student_improvement
- v_recent_submissions
- v_skill_usage_ranking
- v_student_weak_points

#### 第三步：创建应用专用数据库用户（推荐）

为了安全，不要在应用中直接使用root账号：

```sql
-- 创建应用用户（根据实际情况修改密码）
CREATE USER 'vault_app'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';

-- 授予vault数据库的所有权限
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON vault.* TO 'vault_app'@'localhost';

-- 如果需要从其他机器访问（开发团队多人）
CREATE USER 'vault_app'@'%' IDENTIFIED BY 'YourStrongPassword123!';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON vault.* TO 'vault_app'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 验证用户创建成功
SELECT User, Host FROM mysql.user WHERE User = 'vault_app';
```

#### 第四步：测试连接

```bash
# 使用新创建的用户连接
mysql -u vault_app -p -h localhost vault
```

#### 第五步：创建第一个管理员账号

数据库是空的，需要手动创建第一个用户：

```sql
-- 使用BCrypt加密的密码（需要提前用工具生成）
-- 示例：admin123 的BCrypt哈希
INSERT INTO users (username, password, email, nickname, role, status)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMye/IjFfPuLzZlw/2xI5zF.lVe2WpOaGaC',  -- admin123
    'admin@vault.com',
    '系统管理员',
    'ADMIN',
    'ACTIVE'
);

-- 验证创建成功
SELECT id, username, email, nickname, role FROM users;
```

#### 第六步：配置Spring Boot应用

在你的 `application.yml` 或 `application.properties` 中配置：

**application.yml**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vault?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&useSSL=false
    username: vault_app
    password: YourStrongPassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: none  # 重要：不要让JPA自动创建表
    show-sql: true    # 开发环境可以打开，生产环境建议关闭
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.vault.entity
  configuration:
    map-underscore-to-camel-case: true
```

**application.properties**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vault?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=vault_app
spring.datasource.password=YourStrongPassword123!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.vault.entity
mybatis.configuration.map-underscore-to-camel-case=true
```

#### 第七步：在Java中加密密码

需要在代码中使用BCrypt加密：

```java
// 添加依赖（pom.xml）
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

// 在Service中使用
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 注册用户时加密密码
    public void registerUser(String username, String rawPassword, String email) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        // 保存到数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setEmail(email);
        // ...
    }
    
    // 验证密码
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
```

#### 第八步：使用存储过程创建用户（推荐方式）

数据库提供了便捷的存储过程：

```sql
-- 创建学生用户
CALL sp_create_student(
    'student001',                              -- 用户名
    '$2a$10$N9qo8uLOickgx2ZMRZoMye123456',   -- BCrypt密码
    'student001@example.com',                  -- 邮箱
    '张三',                                    -- 昵称
    '2024001',                                 -- 学号
    '计算机科学与技术',                        -- 专业
    '2024级',                                  -- 年级
    '1班',                                     -- 班级
    @user_id,                                  -- 返回：用户ID
    @student_id                                -- 返回：学生ID
);

-- 查看返回值
SELECT @user_id AS user_id, @student_id AS student_id;

-- 创建教师用户
CALL sp_create_teacher(
    'teacher001',                              -- 用户名
    '$2a$10$N9qo8uLOickgx2ZMRZoMye123456',   -- BCrypt密码
    'teacher001@example.com',                  -- 邮箱
    '李老师',                                  -- 昵称
    'T2024001',                                -- 工号
    '计算机科学与技术系',                      -- 院系
    '副教授',                                  -- 职称
    @user_id,                                  -- 返回：用户ID
    @teacher_id                                -- 返回：教师ID
);

-- 查看返回值
SELECT @user_id AS user_id, @teacher_id AS teacher_id;
```

#### 第九步：验证应用连接

启动Spring Boot应用，查看日志：

```
Hikari Connection Pool started
Successfully acquired change log lock
```

---

### 方式二：重新安装数据库（从头开始）

如果你想在自己的机器上重新安装：

#### 步骤1：下载数据库脚本

确保你有以下文件：
```
database/
├── 00_install_structure_only.sql  (生产环境-无测试数据)
├── 00_install_all.sql             (开发环境-含测试数据)
├── 01_create_database.sql
├── 02_create_tables.sql
├── 03_create_views.sql
├── 04_create_procedures.sql
├── 05_create_triggers.sql
├── 06_create_functions.sql
├── 07_create_indexes.sql
└── 08_insert_initial_data.sql     (可选-测试数据)
```

#### 步骤2：选择安装方式

**方式A：生产环境安装（无测试数据）**
```bash
# PowerShell
cd C:\Users\Lenovo\Desktop\Vault\database
Get-Content "00_install_structure_only.sql" | mysql -u root -p --default-character-set=utf8mb4

# Linux/Mac
cd /path/to/Vault/database
mysql -u root -p < 00_install_structure_only.sql
```

**方式B：开发环境安装（含测试数据）**
```bash
# PowerShell
cd C:\Users\Lenovo\Desktop\Vault\database
Get-Content "00_install_all.sql" | mysql -u root -p --default-character-set=utf8mb4

# Linux/Mac
cd /path/to/Vault/database
mysql -u root -p < 00_install_all.sql
```

#### 步骤3：验证安装

```sql
USE vault;

-- 查看表数量（应该是19）
SELECT COUNT(*) AS table_count 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'vault' AND TABLE_TYPE = 'BASE TABLE';

-- 查看视图数量（应该是16）
SELECT COUNT(*) AS view_count 
FROM information_schema.VIEWS 
WHERE TABLE_SCHEMA = 'vault';

-- 查看存储过程数量（应该是12）
SELECT COUNT(*) AS procedure_count 
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = 'vault' AND ROUTINE_TYPE = 'PROCEDURE';
```

---

## 📊 常用数据库操作

### 1. 查询用户信息

```sql
-- 查看所有用户
SELECT * FROM v_user_full_info;

-- 查看学生排行榜
SELECT * FROM v_student_ranking LIMIT 10;

-- 查看某个学生的详细信息
SELECT * FROM v_user_full_info WHERE username = 'student001';
```

### 2. 创建问卷

```sql
-- 插入问卷
INSERT INTO questionnaires (title, description, type, status, creator_id, start_time, end_time)
VALUES (
    '第一章测试',
    'Java基础知识测试',
    'EXAM',
    'PUBLISHED',
    1,  -- 教师ID
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY)
);

-- 插入题目
INSERT INTO questions (questionnaire_id, question_text, question_type, points, correct_answer)
VALUES (
    1,  -- 问卷ID
    'Java是什么类型的语言？',
    'SINGLE_CHOICE',
    5,
    'A'
);
```

### 3. 提交问卷

```sql
-- 使用存储过程提交问卷
CALL sp_submit_questionnaire(
    1,          -- 问卷ID
    1,          -- 学生ID
    @score,     -- 返回：总分
    @submission_id  -- 返回：提交ID
);

-- 查看结果
SELECT @score AS total_score, @submission_id AS submission_id;
```

### 4. 上传教学资料

```sql
INSERT INTO materials (
    title,
    description,
    file_url,
    file_type,
    file_size,
    uploader_id,
    category,
    status
)
VALUES (
    'Java入门教程',
    '适合初学者的Java教程',
    'https://example.com/java-tutorial.pdf',
    'PDF',
    1024000,
    1,  -- 教师ID
    'COURSE',
    'PUBLISHED'
);
```

### 5. 查看统计数据

```sql
-- 问卷统计
SELECT * FROM v_questionnaire_stats WHERE questionnaire_id = 1;

-- 班级成绩统计
CALL sp_get_class_stats('2024级', '1班');

-- 学生薄弱知识点
CALL sp_get_student_weak_points(1, 5);  -- 学生ID, 返回前5个

-- 题目准确率
SELECT * FROM v_question_accuracy WHERE questionnaire_id = 1;
```

---

## 🔐 安全建议

### 1. 数据库用户权限

```sql
-- ✅ 推荐：为应用创建专用用户，只授予必要权限
CREATE USER 'vault_app'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON vault.* TO 'vault_app'@'localhost';

-- ❌ 不推荐：在生产环境使用root用户
-- 不要在应用配置中使用 root 账号
```

### 2. 密码加密

```sql
-- ✅ 推荐：使用BCrypt加密
-- 在Java中使用BCryptPasswordEncoder

-- ❌ 不推荐：明文存储密码
-- 不要存储 '123456' 这样的明文密码
```

### 3. SQL注入防护

```java
// ✅ 推荐：使用预编译语句
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, username);

// ❌ 不推荐：字符串拼接
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

---

## 🔧 常见问题

### Q1: 如何重置数据库？

```sql
-- 删除数据库
DROP DATABASE IF EXISTS vault;

-- 重新安装
-- 运行 00_install_structure_only.sql 或 00_install_all.sql
```

### Q2: 如何备份数据库？

```bash
# 完整备份
mysqldump -u root -p vault > vault_backup_20260121.sql

# 仅备份结构
mysqldump -u root -p --no-data vault > vault_structure.sql

# 仅备份数据
mysqldump -u root -p --no-create-info vault > vault_data.sql
```

### Q3: 如何恢复备份？

```bash
# 恢复完整备份
mysql -u root -p vault < vault_backup_20260121.sql

# 或使用PowerShell
Get-Content "vault_backup_20260121.sql" | mysql -u root -p vault
```

### Q4: 如何查看存储过程代码？

```sql
SHOW CREATE PROCEDURE sp_create_student;
SHOW CREATE FUNCTION fn_calculate_total_score;
```

### Q5: 连接错误怎么办？

```sql
-- 检查用户权限
SHOW GRANTS FOR 'vault_app'@'localhost';

-- 检查用户是否存在
SELECT User, Host FROM mysql.user WHERE User = 'vault_app';

-- 重置密码
ALTER USER 'vault_app'@'localhost' IDENTIFIED BY 'NewPassword123!';
FLUSH PRIVILEGES;
```

---

## 📞 技术支持

### 数据库信息
- **名称**: vault
- **版本**: 1.0
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **时区**: Asia/Shanghai (+08:00)
- **引擎**: InnoDB

### 相关文档
- [数据库设计文档](../test/docs/design/数据库设计文档.md)
- [后端技术文档](../test/docs/design/后端技术文档.md)
- [安装成功说明](INSTALLATION_SUCCESS.md)

### 默认测试账号（仅00_install_all.sql）
- **管理员**: admin / admin123
- **教师**: teacher001 / teacher123  
- **学生**: student001 / student123

⚠️ **注意**: 生产环境请立即修改默认密码！

---

**最后更新**: 2026-01-21  
**数据库版本**: 1.0  
**状态**: ✅ 生产就绪
