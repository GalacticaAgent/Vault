# Vault 部署指南

本文档说明如何部署 Vault 智能教学系统。

## 系统要求

### 后端
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x
- Elasticsearch 8.x
- **Milvus 2.3+ (可选，用于 RAG 功能)**
- **Docker Desktop (推荐，用于运行 Milvus)**

### 前端
- Node.js 16+
- npm 或 pnpm

## 部署步骤

### 0. 启动 Milvus（可选但推荐）

#### 使用 Docker 启动 Milvus

**Windows:**

```powershell
# 运行启动脚本
.\start-milvus.ps1
```

或手动启动：

```powershell
docker-compose -f docker-compose-milvus.yml up -d
```

**验证 Milvus 运行:**

```powershell
docker-compose -f docker-compose-milvus.yml ps
```

应看到 3 个容器在运行：
- milvus-standalone (端口 19530)
- milvus-etcd
- milvus-minio (端口 9000, 9001)

**配置说明:**
- 如果不启动 Milvus，系统会使用模拟数据（仍可运行但 RAG 功能受限）
- 详细安装指南参见: [MILVUS_SETUP.md](MILVUS_SETUP.md)

### 1. 数据库初始化

#### 1.1 启动MySQL

确保MySQL服务正在运行。

#### 1.2 创建数据库和表

```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source vault-backend/src/main/resources/sql/schema.sql

# 插入测试数据（可选）
source vault-backend/src/main/resources/sql/data.sql
```

或者直接在MySQL客户端中执行SQL文件内容。

### 2. 配置后端

#### 2.1 修改数据库配置

编辑 `vault-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vault?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的MySQL密码
```

#### 2.2 配置Redis

确保Redis服务正在运行，默认配置：
- 主机：localhost
- 端口：6379

如需修改，编辑 `application.yml` 中的Redis配置。

#### 2.3 配置Elasticsearch

确保Elasticsearch服务正在运行，默认配置：
- URI：http://localhost:9200

如需修改，编辑 `application.yml` 中的Elasticsearch配置。

### 3. 启动后端服务

#### 方法1：使用Maven

```bash
cd vault-backend
mvn spring-boot:run
```

#### 方法2：使用启动脚本（Windows）

```bash
cd vault-backend
start.bat
```

#### 方法3：打包后运行

```bash
cd vault-backend
mvn clean package
java -jar target/vault-backend-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动。

### 4. 启动前端服务

#### 4.1 安装依赖

```bash
cd vault-frontend
npm install
# 或
pnpm install
```

#### 4.2 运行开发服务器

```bash
npm run dev
# 或
pnpm dev
```

前端应用将在 `http://localhost:5173` 启动。

### 5. 访问应用

打开浏览器访问：http://localhost:5173

## 测试账号

### 学生账号
- 用户名：`student1`
- 密码：`password123`
- 学号：2024001

### 教师账号
- 用户名：`teacher1`
- 密码：`password123`
- 工号：T001

## 验证部署

### 1. 检查后端API

访问 Swagger文档：http://localhost:8080/doc.html

### 2. 测试登录接口

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student1","password":"password123"}'
```

### 3. 测试前端登录

1. 访问 http://localhost:5173
2. 点击"立即登录"
3. 输入测试账号信息
4. 验证是否成功登录并跳转到对应角色的首页

## 生产环境部署

### 1. 前端构建

```bash
cd vault-frontend
npm run build
```

构建产物在 `vault-frontend/dist` 目录。

### 2. 后端打包

```bash
cd vault-backend
mvn clean package -DskipTests
```

JAR文件在 `vault-backend/target` 目录。

### 3. 使用Nginx部署前端

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/vault-frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 4. 使用systemd管理后端服务

创建 `/etc/systemd/system/vault-backend.service`：

```ini
[Unit]
Description=Vault Backend Service
After=syslog.target network.target

[Service]
Type=simple
User=vault
WorkingDirectory=/opt/vault-backend
ExecStart=/usr/bin/java -jar /opt/vault-backend/vault-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start vault-backend
sudo systemctl enable vault-backend
```

## 故障排查

### 后端无法启动

1. 检查MySQL是否运行：`mysql -u root -p`
2. 检查Redis是否运行：`redis-cli ping`
3. 检查Elasticsearch是否运行：`curl http://localhost:9200`
4. 查看后端日志：检查控制台输出

### 前端无法连接后端

1. 检查后端是否启动：`curl http://localhost:8080/api/auth/test`
2. 检查浏览器控制台是否有CORS错误
3. 确认Vite代理配置正确

### 登录失败

1. 确认数据库中有测试数据
2. 检查密码是否正确（默认：password123）
3. 查看后端日志中的错误信息

## 许可证

MIT License

