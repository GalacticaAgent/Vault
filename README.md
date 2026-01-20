# Vault/Radiant 智能教学系统

## 项目简介
Vault/Radiant 是一个基于知识图谱和大语言模型的智能教学辅助系统，旨在为学生提供个性化学习支持，为教师提供智能分析工具。

## 核心功能

### 学生端功能
1. **智能问答**
   - 基于 RAG（检索增强生成）的智能对话
   - 自动检索相关学习资料
   - 多轮对话支持
   - 会话历史管理

2. **问卷答题**
   - 在线答题系统
   - 自动批改与评分
   - 针对性题目生成
   - 答题进度保存

3. **个人肖像**
   - 知识点掌握度可视化
   - 薄弱知识点识别
   - 学习行为分析
   - 年度学习报告

4. **代码提交与分析**
   - GitHub/GitLab 仓库连接
   - 代码质量自动分析
   - AI 生成痕迹检测
   - 代码规范检查

5. **可定制看板**
   - 自定义数据卡片
   - 自然语言查询
   - 实时数据更新

### 教师端功能
1. **智能分析对话**
   - 教学问题咨询
   - 学生情况分析
   - 知识图谱查询

2. **问卷创建与管理**
   - 手动创建问卷
   - AI 自动生成问卷
   - 针对学生肖像生成针对性题目
   - 问卷统计分析

3. **学习资料管理**
   - 资料上传与分类
   - 知识图谱关联
   - 自动爬取与更新

4. **学生肖像分析**
   - 单个学生详细分析
   - 知识点掌握度追踪
   - 学习轨迹查看
   - 代码能力评估

5. **班级统计分析**
   - 班级整体数据统计
   - 学生对比分析
   - 知识点掌握分布
   - 薄弱点汇总

## 技术架构

### 后端技术栈
- Java 17 + Spring Boot 3.x
- MyBatis-Plus 3.x
- MySQL 8.0 + Redis 7.x
- Elasticsearch 8.x (全文检索)
- Milvus (向量检索)
- RabbitMQ (消息队列)

### 前端技术栈
- Vue.js 3.x + Vite 4.x
- Element Plus (UI 组件)
- Pinia (状态管理)
- Vue Router (路由)
- ECharts (数据可视化)

### AI 服务
- OpenAI API / 通义千问 / 文心一言
- RAG (检索增强生成)
- 向量相似度检索

## 项目结构
```
151515/
├── vault-backend/          # 后端项目
├── vault-frontend/         # 前端项目
├── database/               # 数据库脚本
├── docs/                   # 项目文档
├── backend_tech_doc.md     # 后端技术文档
├── frontend_tech_doc.md    # 前端技术文档
├── database_design_doc.md  # 数据库设计文档
└── README.md              # 项目说明（本文件）
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 7.x
- Maven 3.6+

### 后端启动
```bash
cd vault-backend
mvn spring-boot:run
```

### 前端启动
```bash
cd vault-frontend
npm install
npm run dev
```

### 数据库初始化
```bash
# 导入数据库脚本
mysql -u root -p < database/schema.sql
mysql -u root -p < database/init_data.sql
```

## 文档链接
- [后端技术文档](./backend_tech_doc.md)
- [前端技术文档](./frontend_tech_doc.md)
- [数据库设计文档](./database_design_doc.md)
- [后端代码说明](./vault-backend/README.md)
- [前端代码说明](./vault-frontend/README.md)

## 开发团队
Vault/Radiant Development Team

## 许可证
MIT License
