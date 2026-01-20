# Vault/Radiant 后端项目

## 项目简介
基于 Spring Boot 3.x 的后端服务，为 Vault/Radiant 智能教学系统提供 API 支持。

## 技术栈
- Java 17+
- Spring Boot 3.x
- MyBatis-Plus 3.x
- MySQL 8.0+
- Redis 7.x
- Elasticsearch 8.x
- Milvus (向量数据库)

## 核心功能模块
- 用户认证与授权
- 知识图谱管理
- 智能对话服务
- 问卷生成与管理
- 代码分析服务
- 学生肖像分析
- 可定制看板

## 开发环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x
- Node.js 16+ (用于前端联调)

## 快速开始
```bash
# 配置数据库连接
# 编辑 src/main/resources/application.yml

# 启动项目
mvn spring-boot:run
```

## 项目结构说明
详见各子目录的 README.md 文件
