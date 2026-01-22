# Vault 系统演示启动指南

## 当前状态
✅ Java 17 已安装
✅ Node.js 24.13.0 已安装
✅ MySQL 服务 (mysqlzt) 正在运行
✅ Redis 服务 (rediszt3) 正在运行
✅ 后端配置已更新（空密码）
⏳ 前端依赖正在安装中...

## 步骤 1: 初始化数据库

由于 mysql 命令不在 PATH 中，请手动执行以下操作：

### 方法 A: 使用 init_db.bat（推荐）
双击运行项目根目录的 `init_db.bat` 文件

### 方法 B: 手动导入
1. 找到你的 MySQL 安装目录中的 mysql.exe
2. 打开命令行，执行：
```bash
cd c:\Users\28384\Desktop\Vault\Vault\database
"你的MySQL路径\mysql.exe" -u root < 00_install_all.sql
```

### 方法 C: 使用 MySQL Workbench
1. 打开 MySQL Workbench
2. 连接到本地 MySQL (root 用户，空密码)
3. 打开并执行 `database/00_install_all.sql`

## 步骤 2: 安装 Maven（如果还没有）

下载地址: https://maven.apache.org/download.cgi

或使用 Chocolatey:
```bash
choco install maven
```

## 步骤 3: 启动后端服务

```bash
cd vault-backend
mvn clean install
mvn spring-boot:run
```

后端将在 http://localhost:8080/api 启动
API 文档: http://localhost:8080/api/doc.html

## 步骤 4: 启动前端服务

```bash
cd vault-frontend
npm run dev
```

前端将在 http://localhost:5173 启动

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 学生 | student001 | student123 |
| 教师 | teacher001 | teacher123 |
| 管理员 | admin | admin123 |

## 功能演示路径

### 学生端演示
1. 使用 student001 登录
2. 查看个人仪表板 - 学习数据可视化
3. 进入智能问答 - 体验 AI 对话（需要配置 AI API）
4. 查看问卷答题 - 在线测试系统
5. 个人画像 - 知识掌握度分析

### 教师端演示
1. 使用 teacher001 登录
2. 查看教师仪表板 - 班级统计数据
3. 问卷管理 - 创建和管理问卷
4. 学生分析 - 查看学生学习情况
5. 资料管理 - 上传学习资料

## 故障排查

### 后端启动失败
- 检查 MySQL 是否运行: `sc query mysqlzt`
- 检查 Redis 是否运行: `sc query rediszt3`
- 检查数据库是否创建: 登录 MySQL 执行 `SHOW DATABASES;`
- 查看后端日志获取详细错误信息

### 前端启动失败
- 删除 node_modules 和 package-lock.json
- 重新运行 `npm install`
- 检查 Node.js 版本: `node --version`

### 数据库连接失败
- 确认 MySQL root 密码（当前配置为空密码）
- 如果密码不是空的，修改 `vault-backend/src/main/resources/application.yml` 中的密码

## 可选服务（高级功能）

以下服务是可选的，不影响基本功能演示：

- **Elasticsearch** (端口 9200) - 全文搜索功能
- **Milvus** (端口 19530) - 向量检索功能
- **AI API** - 智能问答功能需要配置 OpenAI/通义千问/文心一言 API

---
生成时间: 2026-01-21
