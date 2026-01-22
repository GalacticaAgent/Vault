# PDF文档总结

为上传的PDF文档生成结构化总结，包括核心内容、关键概念和重要结论。

## When to use

当用户上传PDF文档并希望快速了解文档主要内容时使用此技能。适用于学术论文、技术文档、课程资料等文本类PDF的内容提取和总结。

## Keywords

- 总结PDF
- 归纳文档
- 分析论文
- 文档摘要
- PDF提取
- 论文分析

## Workflow

### Step 1: Extract document content
从上传的PDF文件中提取文本内容
- Input: PDF文件路径
- Output: 提取的文本内容

### Step 2: Analyze content structure  
分析文档结构，识别章节、段落和关键信息
- Input: 文本内容
- Output: 结构化的内容分析

### Step 3: Generate summary
基于分析结果生成简洁的文档总结
- Input: 结构化分析
- Output:
  - Format: JSON
  - Fields:
    - title: 文档标题
    - summary: 核心内容总结
    - key_points: 关键要点列表
    - conclusion: 主要结论

## Examples

### Example 1
User: 帮我总结一下这个操作系统的PDF文档

Expected:
```json
{
  "title": "操作系统概论",
  "summary": "本文档介绍了操作系统的基本概念、进程管理、内存管理等核心内容",
  "key_points": [
    "进程是程序的执行实例",
    "虚拟内存提高了内存利用率",
    "文件系统负责数据的持久化存储"
  ],
  "conclusion": "操作系统是计算机系统的核心，负责管理硬件资源和提供服务接口"
}
```

### Example 2
User: 这篇论文主要讲了什么内容？

Expected:
```json
{
  "title": "深度学习在图像识别中的应用",
  "summary": "论文探讨了卷积神经网络在计算机视觉任务中的应用",
  "key_points": [
    "CNN能够自动学习图像特征",
    "数据增强技术提高了模型泛化能力",
    "迁移学习可以加速模型训练"
  ],
  "conclusion": "深度学习技术显著提升了图像识别的准确率"
}
```

