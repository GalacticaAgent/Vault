# Vault Backend

Vault 智能教学系统后端服务

## 技术栈

- Java 17+
- Spring Boot 3.x
- MyBatis-Plus 3.x
- MySQL 8.0+
- Redis 7.x
- Elasticsearch 8.x
- JWT
- Swagger/Knife4j

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x
- Elasticsearch 8.x

### 2. 数据库初始化

```bash
# 连接MySQL
mysql -u root -p

# 执行初始化脚本
source src/main/resources/sql/schema.sql
source src/main/resources/sql/data.sql
```

或者直接在MySQL客户端中执行：

```sql
-- 创建数据库和表结构
source src/main/resources/sql/schema.sql

-- 插入测试数据（可选）
source src/main/resources/sql/data.sql
```

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vault?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 4. 运行项目

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run
```

### 5. 访问API文档

启动成功后访问：http://localhost:8080/doc.html

## 测试账号

### 学生账号
- 用户名：student1
- 密码：password123
- 学号：2024001

### 教师账号
- 用户名：teacher1
- 密码：password123
- 工号：T001

## 项目结构

```
vault-backend/
├── src/main/java/com/vault/
│   ├── VaultApplication.java          # 启动类
│   ├── controller/                     # 控制器层
│   │   └── auth/                      # 认证相关接口
│   ├── service/                       # 服务层
│   │   └── auth/                      # 认证服务
│   ├── entity/                        # 实体类
│   │   └── mysql/                     # MySQL实体
│   ├── dto/                           # 数据传输对象
│   │   ├── request/                   # 请求DTO
│   │   └── response/                  # 响应DTO
│   ├── mapper/                        # MyBatis Mapper
│   ├── security/                      # 安全配置
│   ├── exception/                     # 异常处理
│   └── common/                        # 公共类
└── src/main/resources/
    ├── application.yml                # 主配置文件
    ├── application-dev.yml            # 开发环境配置
    ├── application-prod.yml           # 生产环境配置
    └── sql/                           # SQL脚本
        ├── schema.sql                 # 表结构
        └── data.sql                   # 测试数据
```

## API接口

### 认证接口

- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- GET `/api/auth/test` - 测试接口

## 开发说明

### 添加新的实体类

1. 在 `entity/mysql/` 下创建实体类
2. 使用 `@TableName` 注解指定表名
3. 使用 Lombok 注解简化代码

### 添加新的接口

1. 在对应的 `controller` 子目录下创建控制器
2. 在对应的 `service` 子目录下创建服务类
3. 创建对应的请求和响应DTO

### 异常处理

使用 `BusinessException` 抛出业务异常，全局异常处理器会自动捕获并返回统一格式的错误响应。

```java
throw new BusinessException("错误信息");
throw new BusinessException(400, "自定义错误码和信息");
```

## 许可证

MIT License
