# 环境安装指南

本文档说明如何为 Vault 项目安装和配置开发环境。

## 后端环境 (vault-backend)

### 必需环境

1. **JDK 17+**
   - 下载地址: https://www.oracle.com/java/technologies/downloads/#java17
   - 验证安装: `java -version`

2. **Maven 3.6+**
   - 下载地址: https://maven.apache.org/download.cgi
   - 配置环境变量 `MAVEN_HOME` 和 `PATH`
   - 验证安装: `mvn -version`

3. **MySQL 8.0+**
   - 下载地址: https://dev.mysql.com/downloads/mysql/
   - 创建数据库: `CREATE DATABASE vault_db_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
   - 配置连接信息在 `application-dev.yml`

4. **Redis 7.x**
   - 下载地址: https://redis.io/download
   - 验证安装: `redis-cli ping`

### 可选环境（根据需要）

5. **Elasticsearch 8.x** (用于全文搜索)
   - 下载地址: https://www.elastic.co/downloads/elasticsearch
   - 默认端口: 9200

6. **Milvus** (向量数据库，用于知识图谱)
   - 下载地址: https://milvus.io/docs/install_standalone-docker.md
   - 默认端口: 19530

### 安装步骤

1. 进入后端目录:
   ```bash
   cd vault-backend
   ```

2. 安装依赖（Maven 会自动下载）:
   ```bash
   mvn clean install
   ```

3. 配置数据库连接:
   - 编辑 `src/main/resources/application-dev.yml`
   - 修改 MySQL 用户名和密码

4. 启动项目:
   ```bash
   mvn spring-boot:run
   ```

5. 访问 API 文档:
   - http://localhost:8080/api/doc.html

---

## 前端环境 (vault-frontend)

### 必需环境

1. **Node.js 16+**
   - 下载地址: https://nodejs.org/
   - 推荐使用 LTS 版本
   - 验证安装: `node -v` 和 `npm -v`

### 安装步骤

1. 进入前端目录:
   ```bash
   cd vault-frontend
   ```

2. 安装依赖:
   ```bash
   npm install
   ```
   或者使用 pnpm:
   ```bash
   pnpm install
   ```

3. 启动开发服务器:
   ```bash
   npm run dev
   ```

4. 访问应用:
   - http://localhost:5173

---

## 快速检查清单

### 后端检查
- [ ] JDK 17+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] MySQL 8.0+ 已安装并运行
- [ ] Redis 已安装并运行
- [ ] 数据库已创建
- [ ] `mvn clean install` 成功执行
- [ ] 项目可以启动（`mvn spring-boot:run`）

### 前端检查
- [ ] Node.js 16+ 已安装
- [ ] npm 或 pnpm 可用
- [ ] `npm install` 成功执行
- [ ] 开发服务器可以启动（`npm run dev`）

---

## 常见问题

### 后端问题

1. **Maven 下载依赖慢**
   - 配置国内镜像，编辑 `~/.m2/settings.xml`
   - 添加阿里云镜像源

2. **MySQL 连接失败**
   - 检查 MySQL 服务是否运行
   - 检查用户名、密码是否正确
   - 检查数据库是否已创建

3. **端口被占用**
   - 修改 `application.yml` 中的 `server.port`

### 前端问题

1. **npm install 失败**
   - 检查 Node.js 版本是否 >= 16
   - 尝试清除缓存: `npm cache clean --force`
   - 删除 `node_modules` 和 `package-lock.json` 后重新安装

2. **依赖安装慢**
   - 使用国内镜像: `npm config set registry https://registry.npmmirror.com`
   - 或使用 pnpm: `npm install -g pnpm`

---

## 下一步

环境配置完成后，您可以：
1. 查看各子目录的 README.md 了解项目结构
2. 开始开发功能模块
3. 配置 IDE（推荐 IntelliJ IDEA 或 VS Code）

