# 手动初始化Vault数据库指南

如果自动初始化脚本遇到密码问题，请使用以下方法手动初始化数据库：

## 方法1：使用MySQL Workbench

1. **打开MySQL Workbench**
   - 使用您现有的连接或创建新连接
   - 连接到MySQL服务器

2. **执行初始化脚本**
   - 点击菜单：File → Open SQL Script
   - 选择文件：`database\00_install_all.sql`
   - 点击工具栏的"闪电"图标执行脚本

3. **验证数据库创建**
   - 在左侧Schema列表中应该能看到 `vault` 数据库
   - 展开vault数据库，检查是否有以下表：
     - users
     - students
     - teachers
     - questionnaires
     - questions
     - answers
     - 等等

## 方法2：使用命令行（如果知道正确密码）

### 选项A：如果root有密码
```bash
mysql -u root -p < database\00_install_all.sql
```
输入密码后执行

### 选项B：如果root无密码
```bash
mysql -u root < database\00_install_all.sql
```

## 方法3：更新应用配置以匹配MySQL密码

如果您的MySQL root密码是 `Czy@050226`，需要更新 `vault-backend\src\main\resources\application.yml`：

找到这一行：
```yaml
password:
```

改为：
```yaml
password: Czy@050226
```

然后重新运行init_db.bat脚本。

## 初始化成功后

数据库将包含以下测试账户：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 学生 | student001 | student123 |
| 学生 | student002 | student123 |
| 学生 | student003 | student123 |
| 教师 | teacher001 | teacher123 |
| 教师 | teacher002 | teacher123 |
| 管理员 | admin | admin123 |

## 验证数据库

运行 `check_db.bat` 来验证数据库是否正确初始化。

## 继续测试

数据库初始化成功后，参考 `POSTMAN_TESTING_GUIDE.md` 进行API测试。
