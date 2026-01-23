# 🧪 Vault 系统集成测试指南

## 前提条件

✅ 后端已启动 (端口 8080)
- 地址: http://localhost:8080/api
- Swagger: http://localhost:8080/doc.html

## 启动前端

```powershell
# 切换到前端目录
cd C:\Users\Lenovo\Desktop\test\Vault\vault-frontend

# 启动开发服务器
npm run dev
```

等待看到类似输出：
```
  VITE v4.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

## 测试场景

### 1️⃣ 测试登录功能

**步骤：**
1. 打开浏览器访问: http://localhost:5173
2. 使用测试账号登录

**学生账号：**
- 用户名: `student001`
- 密码: `password123`

**教师账号：**
- 用户名: `teacher001`
- 密码: `password123`

**预期结果：**
- ✅ 成功登录并跳转到对应的主页
- ✅ 显示用户信息

---

### 2️⃣ 测试智能问答功能（模拟数据模式）

**步骤：**
1. 使用学生账号登录
2. 点击左侧菜单 "智能问答" 或 "聊天"
3. 在输入框输入问题并发送

**测试问题：**
```
什么是进程？
线程和进程有什么区别？
操作系统的主要功能是什么？
请解释一下虚拟内存的概念
```

**预期结果：**
- ✅ AI 返回回答（基于 DeepSeek）
- ✅ 显示参考资料来源（模拟数据）
  - 操作系统原理 - 第1章 - 进程管理
  - 操作系统原理 - 第2章 - 进程管理
  - 操作系统原理 - 第3章 - 进程管理
- ✅ 可以看到消息历史记录
- ✅ 可以创建新对话

**后端日志检查：**
```
RAG search completed: 3 chunks found in Xms
Milvus 未连接，使用模拟数据
```

---

### 3️⃣ 测试 Skills 功能

**Skills 已加载（后端日志确认）：**
1. ✅ code_review - 代码审查与改进建议
2. ✅ concept_explanation - 知识点讲解
3. ✅ pdf_summary - PDF文档总结
4. ✅ question_generator - 智能题目生成
5. ✅ weak_point_analysis - 薄弱知识点分析

**测试方法 1: 智能问答中触发 Skills**

在智能问答中提问，Skills 会自动被调用：

```
# 触发 concept_explanation
请详细讲解一下进程调度算法

# 触发 code_review（需要先上传代码）
帮我审查这段代码的问题

# 触发 weak_point_analysis（教师端）
分析学生张三的薄弱知识点
```

**测试方法 2: 教师端 Skills 管理**

1. 使用教师账号登录
2. 点击 "Skills 页面"
3. 查看已加载的 Skills 列表

**预期结果：**
- ✅ 看到 5 个 Skills
- ✅ 每个 Skill 显示名称和描述
- ✅ 可以上传新的 SKILL.md 文件

**后端日志检查：**
```
Loaded 5 skills: pdf文档总结, 知识点讲解, 薄弱知识点分析, 智能题目生成, 代码审查与改进建议
```

---

### 4️⃣ 测试文件上传功能

**步骤：**
1. 在智能问答界面
2. 点击附件图标 📎
3. 选择文件上传（支持 PDF、代码文件）

**支持的文件类型：**
- PDF: `.pdf`
- 代码: `.py`, `.java`, `.js`, `.ts`, `.cpp`, `.c`
- 文档: `.txt`, `.md`

**预期结果：**
- ✅ 文件上传成功
- ✅ 显示文件信息（名称、大小、类型）
- ✅ 可以在对话中引用该文件
- ✅ AI 可以基于文件内容回答问题

**后端日志检查：**
```
File uploaded: xxx.pdf by user xxx
文件上传目录初始化成功: C:\Users\Lenovo\Desktop\test\Vault\vault-backend\uploads
```

---

### 5️⃣ 测试对话分享功能

**步骤：**
1. 在智能问答中进行对话
2. 点击右上角 "分享" 按钮
3. 设置分享选项：
   - 标题
   - 描述
   - 是否需要密码
   - 过期时间（天数）

**预期结果：**
- ✅ 生成分享链接
- ✅ 可以复制链接
- ✅ 访问链接可以看到对话内容
- ✅ 需要密码的分享会要求输入密码

---

### 6️⃣ 测试学生看板功能

**步骤：**
1. 使用学生账号登录
2. 点击 "看板" 或 "Dashboard"

**预期显示：**
- ✅ 学号
- ✅ 提问总数
- ✅ 问卷分数和排名
- ✅ 其他统计信息

---

### 7️⃣ 测试教师看板功能

**步骤：**
1. 使用教师账号登录
2. 点击 "看板" 或 "Dashboard"
3. 可以提问：
   - "这个班最不了解进程概念的同学是谁？"
   - "这个班最活跃的五名同学是谁？"

**预期结果：**
- ✅ 显示班级统计信息
- ✅ AI 基于数据回答问题

---

## 🔍 API 测试（使用 Swagger）

访问: http://localhost:8080/doc.html

### 测试智能问答 API

1. 找到 `/chat/send` 接口
2. 点击 "调试"
3. 输入请求体：

```json
{
  "chatId": null,
  "content": "什么是进程？",
  "fileIds": [],
  "model": "deepseek-chat",
  "temperature": 0.7,
  "maxTokens": 4096,
  "stream": false
}
```

4. 需要先登录获取 token，然后在 "Authorization" 填入: `Bearer <your-token>`

**预期响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "content": "进程是...",
    "role": "assistant",
    "references": [...],
    "skillUsed": null,
    "createTime": "2026-01-23T..."
  }
}
```

---

## 📊 验证 RAG 功能

### 检查后端日志

应该看到：
```
RAG search completed: X chunks found in Xms
相关参考资料：
[参考资料1] 操作系统原理 - 第1章
[参考资料2] 操作系统原理 - 第2章
```

### 检查前端显示

AI 回答下方应显示：
```
📚 参考资料：
• 操作系统原理 - 第1章 - 进程管理
• 操作系统原理 - 第2章 - 进程管理
```

---

## 🐛 常见问题排查

### 问题 1: 前端无法连接后端

**症状：** 登录失败、API 调用失败

**检查：**
```powershell
# 测试后端是否运行
curl http://localhost:8080/api/health

# 或在浏览器打开
http://localhost:8080/doc.html
```

**解决：** 确保后端正在运行

---

### 问题 2: 智能问答无响应

**症状：** 发送消息后没有回复

**检查后端日志：**
- 是否有异常
- DeepSeek API 调用是否成功

**解决：**
- 检查 `vault.ai.api-key` 配置
- 检查网络连接

---

### 问题 3: 文件上传失败

**症状：** 上传文件时报错

**检查：**
- 文件大小是否超过 10MB
- 文件类型是否支持

**后端配置：**
```yaml
vault:
  file:
    upload-dir: uploads
    max-size: 10485760  # 10MB
    allowed-types: pdf,java,py,js,ts,md,txt
```

---

## ✅ 测试检查清单

### 基础功能
- [ ] 学生登录成功
- [ ] 教师登录成功
- [ ] 看板显示正常

### 智能问答
- [ ] 发送消息成功
- [ ] 收到 AI 回复
- [ ] 显示参考资料（模拟数据）
- [ ] 创建新对话成功
- [ ] 查看历史对话成功

### Skills 功能
- [ ] Skills 列表加载成功（5个）
- [ ] 对话中 Skills 自动触发
- [ ] 教师端可以管理 Skills

### 文件功能
- [ ] PDF 上传成功
- [ ] 代码文件上传成功
- [ ] 文件内容提取成功
- [ ] 基于文件内容问答成功

### 分享功能
- [ ] 创建分享链接成功
- [ ] 访问分享链接可查看内容
- [ ] 密码保护功能正常

---

## 📝 测试记录模板

```
测试时间：2026-01-23
测试人员：
后端版本：1.0.0
前端版本：1.0.0

功能 | 状态 | 备注
-----|------|------
登录 | ✅ | 
智能问答 | ✅ | RAG 使用模拟数据
Skills | ✅ | 加载5个技能
文件上传 | ✅ | 
对话分享 | ✅ | 
```

---

## 🎯 下一步

测试完成后，可以：

1. **启用完整 RAG 功能**
   - 安装 Docker Desktop
   - 运行 Milvus
   - 配置 Embedding API
   - 参考: [MILVUS_SETUP.md](MILVUS_SETUP.md)

2. **上传真实教材**
   - 使用教师账号
   - 上传 PDF 教材
   - 系统会自动建立索引

3. **性能优化**
   - 配置 Redis 缓存
   - 优化数据库查询
   - 调整 AI 参数

---

**祝测试顺利！** 🎉

有任何问题请查看日志或联系开发团队。
