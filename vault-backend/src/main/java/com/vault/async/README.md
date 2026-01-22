# Async 异步任务层

## 职责
处理异步任务，如代码分析、问卷生成等耗时操作。

## 主要类

### CodeAnalysisTask.java
代码分析异步任务
- 代码仓库克隆
- 代码质量分析
- AI 检测

### QuestionnaireGenerationTask.java
问卷生成异步任务
- 问卷生成
- 题目生成
- 答案校验

## 使用场景
- 代码分析（耗时操作）
- 问卷生成（AI 调用）
- 批量数据处理

