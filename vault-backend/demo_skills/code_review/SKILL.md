# 代码审查与改进建议

对学生提交的代码进行全面审查，指出潜在问题、提供改进建议，并评估代码质量。

## When to use

当用户上传代码文件并需要代码审查、质量评估或改进建议时使用此技能。适用于各种编程语言的代码分析。

## Keywords

- 代码审查
- Code Review
- 代码分析
- 改进建议
- 代码质量
- 优化代码
- 代码规范
- 检查代码

## Workflow

### Step 1: Parse code content
解析代码文件，识别编程语言和代码结构
- Input: 代码文件内容
- Output: 
  - language: 编程语言
  - structure: 代码结构（类、函数、变量等）

### Step 2: Analyze code quality
从多个维度分析代码质量
- Input: 代码结构
- Analysis dimensions:
  - **正确性**: 逻辑错误、潜在 bug
  - **可读性**: 命名规范、注释质量
  - **性能**: 时间复杂度、空间复杂度
  - **安全性**: 安全漏洞、输入验证
  - **规范性**: 编码风格、最佳实践
- Output: 各维度的评分和问题列表

### Step 3: Detect AI usage
检测代码是否由 AI 生成或辅助编写
- Input: 代码内容、编码风格
- Detection indicators:
  - 过于标准化的命名
  - 完美的代码格式
  - 缺少个人编码习惯
  - 注释风格统一度异常高
- Output: AI 使用概率（0-100%）

### Step 4: Generate review report
生成详细的代码审查报告
- Input: 质量分析结果、AI 检测结果
- Output:
  - Format: JSON
  - Fields:
    - overall_score: 总体评分（0-100）
    - language: 编程语言
    - ai_probability: AI 使用概率
    - strengths: 优点列表
    - issues: 问题列表
      - type: 问题类型（bug/style/performance/security）
      - severity: 严重程度（critical/major/minor）
      - line: 问题所在行号
      - description: 问题描述
      - suggestion: 改进建议
    - recommendations: 总体改进建议

## Examples

### Example 1: Python 代码审查
User: 帮我看看这段 Python 代码有什么问题

Code:
```python
def find_max(lst):
    max = lst[0]
    for i in range(len(lst)):
        if lst[i] > max:
            max = lst[i]
    return max
```

Expected Output:
```json
{
  "overall_score": 65,
  "language": "Python",
  "ai_probability": 15,
  "strengths": [
    "逻辑正确，能够找到最大值",
    "代码结构简洁清晰"
  ],
  "issues": [
    {
      "type": "bug",
      "severity": "critical",
      "line": 2,
      "description": "未处理空列表情况，会导致 IndexError",
      "suggestion": "添加列表为空的判断: if not lst: return None"
    },
    {
      "type": "style",
      "severity": "minor",
      "line": 2,
      "description": "变量名 'max' 会覆盖内置函数",
      "suggestion": "改为 'max_value' 或 'maximum'"
    },
    {
      "type": "performance",
      "severity": "minor",
      "line": 3,
      "description": "使用 range(len()) 不是 Pythonic 的写法",
      "suggestion": "直接遍历列表: for num in lst"
    }
  ],
  "recommendations": [
    "使用 Python 内置的 max() 函数更简洁",
    "添加类型提示提高代码可维护性",
    "考虑添加文档字符串说明函数功能"
  ]
}
```

### Example 2: 检测 AI 生成代码
User: 这段代码是我自己写的吗？

Code:
```java
/**
 * Calculates the factorial of a given non-negative integer.
 *
 * @param n the non-negative integer
 * @return the factorial of n
 * @throws IllegalArgumentException if n is negative
 */
public static long calculateFactorial(int n) {
    if (n < 0) {
        throw new IllegalArgumentException("Input must be non-negative");
    }
    
    long result = 1L;
    for (int i = 2; i <= n; i++) {
        result *= i;
    }
    
    return result;
}
```

Expected Output:
```json
{
  "overall_score": 95,
  "language": "Java",
  "ai_probability": 85,
  "strengths": [
    "完整的 JavaDoc 文档",
    "异常处理规范",
    "命名符合驼峰规范",
    "代码格式完美"
  ],
  "issues": [
    {
      "type": "performance",
      "severity": "minor",
      "line": 0,
      "description": "未考虑大数溢出问题",
      "suggestion": "对于大于 20 的 n，long 会溢出，建议使用 BigInteger"
    }
  ],
  "ai_detection_indicators": [
    "JavaDoc 注释格式过于标准化和完整",
    "变量命名过于规范，缺少个人风格",
    "代码风格与该学生以往提交的代码差异明显",
    "异常处理信息使用标准英文表述"
  ],
  "recommendations": [
    "代码质量很高，但建议保持个人编码风格",
    "如果使用了 AI 辅助，建议在注释中说明"
  ]
}
```

## Notes

- 对于不同编程语言，应使用对应的最佳实践标准
- AI 检测仅供参考，不应作为唯一判断依据
- 应结合学生历史代码风格进行对比分析
- 鼓励学生在理解的基础上使用 AI 辅助，而非直接复制
