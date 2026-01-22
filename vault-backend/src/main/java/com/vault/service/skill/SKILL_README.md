# Skills系统说明

## 概述

Skills系统是Vault智能问答的核心功能之一，参考了 [chat-skills](https://github.com/unitial/chat-skills) 项目的设计理念。

## 系统架构

```
skill/
├── SkillDefinition.java    # Skill定义
├── SkillLoader.java        # Skill加载器
├── SkillExecutor.java      # Skill执行器
├── SkillRouter.java        # Skill路由器
├── SkillContext.java       # 执行上下文
└── SkillResult.java        # 执行结果
```

## Skill定义格式

Skill使用Markdown格式定义，存放在`skills/`目录下。

### 目录结构示例

```
skills/
├── knowledge_explanation/
│   └── SKILL.md
├── code_analysis/
│   └── SKILL.md
├── material_recommendation/
│   └── SKILL.md
└── general_chat/
    └── SKILL.md
```

### SKILL.md格式

```markdown
# 知识点讲解

## Description
为学生讲解特定的知识点，结合教材内容和示例。

## Triggers
- 不理解
- 是什么
- 讲解
- 解释

## Workflow

1. 从用户问题中提取知识点关键词
2. 在向量数据库中检索相关教材内容（RAG）
3. 调用LLM生成通俗易懂的讲解
4. 推荐相关的学习资料和练习题

## Output
输出格式为Markdown，包含：
- 知识点定义
- 详细讲解
- 代码示例（如果适用）
- 推荐资料链接
```

## 工作流程

### 1. Skill加载

系统启动时，`SkillLoader`会：
- 扫描`skills/`目录
- 解析每个Skill的`SKILL.md`文件
- 将Skill定义加载到内存
- 监听文件变化，支持热更新

### 2. Skill路由

当用户发送消息时，`SkillRouter`会：
- 分析用户输入的意图
- 匹配合适的Skill（基于triggers关键词）
- 计算相关性得分，选择最佳Skill
- 如果没有匹配，使用通用对话Skill

### 3. Skill执行

`SkillExecutor`执行Skill的工作流：
- 初始化执行上下文（包含用户输入、历史消息等）
- 逐步执行workflow中定义的步骤
- 每个步骤可以是：
  - `rag_search`: 检索相关资料
  - `llm_call`: 调用AI生成内容
  - `knowledge_query`: 查询知识图谱
  - `data_process`: 数据处理
- 将步骤结果保存到上下文变量
- 根据输出模板格式化最终结果

## 创建自定义Skill

### 步骤1：创建Skill目录

```bash
mkdir -p skills/my_custom_skill
cd skills/my_custom_skill
```

### 步骤2：编写SKILL.md

```markdown
# 我的自定义Skill

## Description
这是一个自定义Skill的描述

## Triggers
- 关键词1
- 关键词2

## Workflow

1. 步骤1：检索资料
   - type: rag_search
   - parameters:
     - top_k: 5

2. 步骤2：调用AI
   - type: llm_call
   - parameters:
     - prompt_template: "基于以下资料：{rag_results}\n回答问题：{user_input}"

## Output
输出格式：{ai_response}
```

### 步骤3：重启服务或等待热加载

系统会自动检测到新的Skill并加载。

## 预定义的Skill

### 1. knowledge_explanation (知识点讲解)

**用途**：讲解CS课程中的知识点

**触发词**：不理解、是什么、讲解、解释

**工作流**：
1. 提取知识点
2. RAG检索教材内容
3. AI生成讲解
4. 推荐学习资料

### 2. code_analysis (代码分析)

**用途**：分析学生代码，找出bug和不规范之处

**触发词**：代码、程序、bug、错误

**工作流**：
1. 获取代码内容
2. 静态分析
3. AI评审代码
4. 给出改进建议

### 3. material_recommendation (资料推荐)

**用途**：根据学生的薄弱点推荐学习资料

**触发词**：推荐、学习资料、练习

**工作流**：
1. 查询学生肖像
2. 识别薄弱知识点
3. 从知识图谱检索相关资料
4. 生成个性化推荐列表

### 4. general_chat (通用对话)

**用途**：处理不需要特殊技能的普通对话

**触发词**：（默认，无特定触发词）

**工作流**：
1. 简单的问候或确认
2. 直接调用AI回复

## 技术实现

### SkillLoader

```java
// 加载所有Skills
public void loadAllSkills() {
    // 扫描skills目录
    // 解析SKILL.md
    // 存储到内存Map
}

// 匹配Skills
public List<SkillDefinition> matchSkills(String input) {
    // 根据triggers匹配
    // 返回匹配的Skills列表
}
```

### SkillRouter

```java
// 路由到最佳Skill
public SkillDefinition route(String userInput) {
    // 1. 关键词匹配
    // 2. 语义分析
    // 3. 计算相关性得分
    // 4. 返回最佳Skill
}
```

### SkillExecutor

```java
// 执行Skill
public SkillResult execute(SkillDefinition skill, SkillContext context) {
    // 1. 初始化上下文
    // 2. 逐步执行workflow
    // 3. 格式化输出
    // 4. 返回结果
}
```

## 高级特性

### 1. 变量传递

在workflow中，可以使用变量：

```markdown
## Workflow

1. 检索资料
   - output_variable: rag_results

2. 生成回答
   - parameters:
     - prompt: "资料：{rag_results}\n问题：{user_input}"
```

### 2. 条件执行

可以根据条件执行不同的步骤（待实现）。

### 3. 并行执行

多个独立步骤可以并行执行（待实现）。

## 注意事项

1. Skill ID必须唯一
2. triggers要准确，避免过度匹配
3. workflow步骤要清晰，避免循环依赖
4. 输出格式要统一，便于前端展示

## 扩展建议

1. 添加更多预定义Skills
2. 支持YAML格式的Skill定义
3. 实现Skill的版本管理
4. 添加Skill的测试用例
5. 提供Skill编辑器UI
