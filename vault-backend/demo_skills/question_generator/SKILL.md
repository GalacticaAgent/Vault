# 智能题目生成

根据知识点、难度要求和学生情况，自动生成高质量的测试题目，支持多种题型。

## When to use

当教师需要快速出题、或者需要针对学生薄弱点生成个性化练习题时使用。

## Keywords

- 生成题目
- 出题
- 创建问卷
- 练习题
- 考试题
- 测试题
- 针对性出题
- 个性化题目

## Workflow

### Step 1: Parse generation requirements
解析题目生成要求
- Input: 教师的出题描述或配置
- Parse elements:
  - knowledge_points: 涉及的知识点列表
  - difficulty_distribution: 难度分布（easy/medium/hard）
  - question_types: 题型分布（选择/判断/简答/编程）
  - quantity: 每种题型的数量
  - personalization: 是否个性化（针对学生薄弱点）
- Output: 结构化的出题需求

### Step 2: Query student profiles (if personalized)
如果是个性化出题，查询学生画像
- Input: 学生 ID 列表
- Query from Neo4j:
  - 每个学生的薄弱知识点（掌握度 < 0.6）
  - 常犯错误类型
  - 历史答题记录
- Output: 
  - weak_concepts_per_student: 每个学生的薄弱点
  - target_knowledge_points: 需要重点考查的知识点

### Step 3: Retrieve reference questions
从题库检索参考题目
- Input: 知识点、难度
- Query from MySQL question_bank:
  - 相关知识点的历史题目
  - 相似难度的题目
  - 高质量题目（好评率高）
- Output: 参考题目列表（用于 AI 学习出题风格）

### Step 4: Generate questions with AI
使用 AI 生成题目
- Input: 需求、学生画像、参考题目
- AI Prompt structure:
  ```
  你是一位经验丰富的计算机课程教师，需要出一套测试题。
  
  要求：
  - 知识点：{knowledge_points}
  - 难度：{difficulty}
  - 题型：{type}
  - 个性化：针对以下学生薄弱点 {weak_points}
  
  参考题目风格：
  {reference_questions}
  
  请生成题目，格式为 JSON...
  ```
- Output: AI 生成的题目（JSON 格式）

### Step 5: Validate and refine questions
验证和优化题目质量
- Validation checks:
  - **答案正确性**: 检查标准答案是否正确
  - **选项合理性**: 干扰选项是否有迷惑性
  - **难度匹配**: 题目难度是否符合要求
  - **知识点覆盖**: 是否准确考查目标知识点
  - **语言规范**: 题干表述是否清晰无歧义
- Refinement:
  - 如果验证失败，要求 AI 重新生成
  - 调整干扰选项的迷惑性
  - 优化题干表述
- Output: 经过验证的高质量题目

### Step 6: Format and save questions
格式化并保存题目
- Input: 验证后的题目
- Operations:
  - 分配题目 ID
  - 保存到 MySQL questions 表
  - 建立知识点关联（保存到 Neo4j）
  - 生成题目预览（Markdown 格式）
- Output:
  - Format: JSON array
  - Fields per question:
    - id: 题目 ID
    - type: 题型
    - content: 题干
    - options: 选项（选择题）
    - answer: 标准答案
    - explanation: 答案解析
    - difficulty: 难度
    - knowledge_points: 关联知识点
    - estimated_time: 预计用时

## Examples

### Example 1: 基础题目生成
Input:
```json
{
  "knowledge_points": ["进程状态转换"],
  "difficulty": "easy",
  "type": "single_choice",
  "quantity": 3,
  "personalized": false
}
```

Expected Output:
```json
{
  "questions": [
    {
      "id": "Q001",
      "type": "single_choice",
      "difficulty": "easy",
      "content": "进程从运行态转换到阻塞态的原因是？",
      "options": [
        "A. 时间片用完",
        "B. 等待 I/O 操作完成",
        "C. 被高优先级进程抢占",
        "D. 进程执行完毕"
      ],
      "answer": "B",
      "explanation": "当进程需要等待 I/O 操作或其他事件完成时，会从运行态转换到阻塞态。时间片用完会转到就绪态，执行完毕转到终止态。",
      "knowledge_points": ["进程状态转换", "I/O管理"],
      "estimated_time": 60,
      "tags": ["概念理解", "状态转换"]
    },
    {
      "id": "Q002",
      "type": "single_choice",
      "difficulty": "easy",
      "content": "以下哪个不是进程的基本状态？",
      "options": [
        "A. 运行态",
        "B. 就绪态",
        "C. 阻塞态",
        "D. 挂起态"
      ],
      "answer": "D",
      "explanation": "进程的三个基本状态是运行态、就绪态和阻塞态。挂起态是扩展状态，不属于基本状态。",
      "knowledge_points": ["进程状态"],
      "estimated_time": 60,
      "tags": ["概念记忆"]
    },
    {
      "id": "Q003",
      "type": "single_choice",
      "difficulty": "easy",
      "content": "就绪态进程获得 CPU 后进入什么状态？",
      "options": [
        "A. 阻塞态",
        "B. 运行态",
        "C. 终止态",
        "D. 创建态"
      ],
      "answer": "B",
      "explanation": "就绪态进程在获得 CPU 资源后，立即转入运行态开始执行。",
      "knowledge_points": ["进程调度", "进程状态转换"],
      "estimated_time": 45,
      "tags": ["状态转换"]
    }
  ],
  "summary": {
    "total": 3,
    "difficulty_distribution": {
      "easy": 3,
      "medium": 0,
      "hard": 0
    },
    "estimated_total_time": 165
  }
}
```

### Example 2: 个性化题目生成
Input:
```json
{
  "knowledge_points": ["内存管理", "虚拟内存"],
  "difficulty": "medium",
  "type": "short_answer",
  "quantity": 2,
  "personalized": true,
  "student_ids": [1001, 1002],
  "student_weak_points": {
    "1001": ["页面置换算法", "缺页中断"],
    "1002": ["地址映射", "页表结构"]
  }
}
```

Expected Output:
```json
{
  "questions": [
    {
      "id": "Q101",
      "type": "short_answer",
      "difficulty": "medium",
      "content": "某系统采用页式虚拟存储管理，页面大小为 4KB，进程访问虚拟地址 0x2A3C，此时页表如下：\n\n| 页号 | 页框号 | 有效位 |\n|------|--------|--------|\n| 0    | 5      | 1      |\n| 1    | -      | 0      |\n| 2    | 3      | 1      |\n| 3    | 7      | 1      |\n\n请回答：\n1. 该虚拟地址对应的页号和页内偏移各是多少？\n2. 能否成功访问？如果能，物理地址是多少？如果不能，会发生什么？",
      "answer": "1. 页号 = 0x2A3C ÷ 4KB = 0x2A3C ÷ 0x1000 = 2，页内偏移 = 0x2A3C % 0x1000 = 0xA3C\n2. 能成功访问。页号 2 的有效位为 1，页框号为 3，所以物理地址 = 3 × 4KB + 0xA3C = 0x3A3C",
      "explanation": "解题步骤：\n1. 计算页号：虚拟地址 ÷ 页面大小（向下取整）\n2. 计算页内偏移：虚拟地址 % 页面大小\n3. 查页表：检查有效位，获取页框号\n4. 计算物理地址：页框号 × 页面大小 + 页内偏移\n\n考点：地址映射过程、页表结构",
      "knowledge_points": ["地址映射", "页表", "虚拟内存"],
      "estimated_time": 300,
      "tags": ["计算题", "地址转换"],
      "personalized_for": [1002],
      "targets_weak_point": "地址映射"
    },
    {
      "id": "Q102",
      "type": "short_answer",
      "difficulty": "medium",
      "content": "系统采用 LRU 页面置换算法，内存分配 3 个页框，初始为空。进程依次访问页面序列：1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5\n\n请：\n1. 画出页面置换过程（建议用表格形式）\n2. 计算缺页次数和缺页率\n3. 说明第 7 次访问（页面 5）时，为什么选择置换特定页面",
      "answer": "1. 置换过程：\n\n| 访问序列 | 1 | 2 | 3 | 4 | 1 | 2 | 5 | 1 | 2 | 3 | 4 | 5 |\n|---------|---|---|---|---|---|---|---|---|---|---|---|---|\n| 页框1   | 1 | 1 | 1 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 |\n| 页框2   |   | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 2 | 5 |\n| 页框3   |   |   | 3 | 3 | 3 | 3 | 5 | 5 | 5 | 3 | 3 | 3 |\n| 缺页?   | ✓ | ✓ | ✓ | ✓ |   |   | ✓ |   |   | ✓ | ✓ |   |\n\n2. 缺页次数 = 7，缺页率 = 7/12 ≈ 58.3%\n\n3. 第 7 次访问页面 5 时，内存中是 [4, 2, 3]：\n   - 页面 4：最近使用时间是第 4 次访问\n   - 页面 2：最近使用时间是第 6 次访问\n   - 页面 3：最近使用时间是第 3 次访问\n   \n   页面 3 最久未使用，所以被置换出去。",
      "explanation": "LRU（Least Recently Used）算法：\n- 选择最长时间未被访问的页面置换\n- 需要记录每个页面的最近访问时间\n- 实际系统中常用近似算法（因为精确 LRU 开销大）\n\n常见错误：\n- 把 LRU 和 FIFO 混淆\n- 不清楚如何判断"最久未使用"\n- 没有正确更新访问时间",
      "knowledge_points": ["页面置换算法", "LRU", "缺页中断"],
      "estimated_time": 420,
      "tags": ["计算题", "算法应用"],
      "personalized_for": [1001],
      "targets_weak_point": "页面置换算法"
    }
  ],
  "summary": {
    "total": 2,
    "personalization_applied": true,
    "student_coverage": {
      "1001": ["Q102"],
      "1002": ["Q101"]
    },
    "estimated_total_time": 720
  }
}
```

### Example 3: 编程题生成
Input:
```json
{
  "knowledge_points": ["进程同步", "生产者消费者"],
  "difficulty": "hard",
  "type": "programming",
  "quantity": 1,
  "language": "C"
}
```

Expected Output:
```json
{
  "questions": [
    {
      "id": "P001",
      "type": "programming",
      "difficulty": "hard",
      "content": "## 生产者-消费者问题\n\n### 问题描述\n实现一个简化的生产者-消费者模型：\n- 有 1 个生产者线程和 1 个消费者线程\n- 共享一个大小为 5 的环形缓冲区\n- 生产者生成数据放入缓冲区\n- 消费者从缓冲区取出数据\n\n### 要求\n1. 使用信号量实现同步\n2. 生产者生成 0-99 的整数\n3. 消费者打印取出的数据\n4. 不能出现数据丢失或重复消费\n5. 缓冲区满时生产者阻塞，空时消费者阻塞\n\n### 函数原型\n```c\nvoid* producer(void* arg);  // 生产者线程\nvoid* consumer(void* arg);  // 消费者线程\n```\n\n### 输入\n无输入，生产者固定生成 100 个数据\n\n### 输出\n消费者按顺序打印 0-99\n\n### 示例\n```\n// 可能的输出（顺序不定，但数据完整）\nConsumer: 0\nConsumer: 1\nConsumer: 2\n...\nConsumer: 99\n```",
      "starter_code": "#include <stdio.h>\n#include <pthread.h>\n#include <semaphore.h>\n\n#define BUFFER_SIZE 5\n#define DATA_COUNT 100\n\nint buffer[BUFFER_SIZE];\nint in = 0, out = 0;\n\nsem_t empty, full, mutex;\n\nvoid* producer(void* arg) {\n    // TODO: 实现生产者逻辑\n    return NULL;\n}\n\nvoid* consumer(void* arg) {\n    // TODO: 实现消费者逻辑\n    return NULL;\n}\n\nint main() {\n    pthread_t prod_thread, cons_thread;\n    \n    // TODO: 初始化信号量\n    \n    // 创建线程\n    pthread_create(&prod_thread, NULL, producer, NULL);\n    pthread_create(&cons_thread, NULL, consumer, NULL);\n    \n    // 等待线程结束\n    pthread_join(prod_thread, NULL);\n    pthread_join(cons_thread, NULL);\n    \n    // TODO: 销毁信号量\n    \n    return 0;\n}",
      "test_cases": [
        {
          "input": "",
          "expected_output": "0-99的所有数字（顺序可以不同）",
          "description": "验证数据完整性",
          "points": 30
        },
        {
          "input": "",
          "expected_output": "无数据重复或丢失",
          "description": "验证同步正确性",
          "points": 40
        },
        {
          "input": "",
          "expected_output": "程序正常结束，无死锁",
          "description": "验证无死锁",
          "points": 30
        }
      ],
      "reference_answer": "#include <stdio.h>\n#include <pthread.h>\n#include <semaphore.h>\n\n#define BUFFER_SIZE 5\n#define DATA_COUNT 100\n\nint buffer[BUFFER_SIZE];\nint in = 0, out = 0;\n\nsem_t empty, full, mutex;\n\nvoid* producer(void* arg) {\n    for (int i = 0; i < DATA_COUNT; i++) {\n        sem_wait(&empty);  // 等待空槽\n        sem_wait(&mutex);  // 互斥访问缓冲区\n        \n        buffer[in] = i;\n        in = (in + 1) % BUFFER_SIZE;\n        \n        sem_post(&mutex);  // 释放互斥锁\n        sem_post(&full);   // 增加满槽计数\n    }\n    return NULL;\n}\n\nvoid* consumer(void* arg) {\n    for (int i = 0; i < DATA_COUNT; i++) {\n        sem_wait(&full);   // 等待满槽\n        sem_wait(&mutex);  // 互斥访问缓冲区\n        \n        int data = buffer[out];\n        out = (out + 1) % BUFFER_SIZE;\n        printf(\"Consumer: %d\\n\", data);\n        \n        sem_post(&mutex);  // 释放互斥锁\n        sem_post(&empty);  // 增加空槽计数\n    }\n    return NULL;\n}\n\nint main() {\n    pthread_t prod_thread, cons_thread;\n    \n    sem_init(&empty, 0, BUFFER_SIZE);  // 初始全空\n    sem_init(&full, 0, 0);             // 初始无数据\n    sem_init(&mutex, 0, 1);            // 互斥锁\n    \n    pthread_create(&prod_thread, NULL, producer, NULL);\n    pthread_create(&cons_thread, NULL, consumer, NULL);\n    \n    pthread_join(prod_thread, NULL);\n    pthread_join(cons_thread, NULL);\n    \n    sem_destroy(&empty);\n    sem_destroy(&full);\n    sem_destroy(&mutex);\n    \n    return 0;\n}",
      "grading_criteria": [\n        {\n          "criterion": "正确使用信号量",\n          "points": 20,\n          "description": "empty, full, mutex 三个信号量初始化和使用正确"\n        },\n        {\n          "criterion": "环形缓冲区实现",\n          "points": 15,\n          "description": "正确使用取模运算实现环形队列"\n        },\n        {\n          "criterion": "同步逻辑正确",\n          "points": 25,\n          "description": "sem_wait 和 sem_post 顺序正确，无死锁"\n        },\n        {\n          "criterion": "数据完整性",\n          "points": 25,\n          "description": "所有数据正确生产和消费，无丢失或重复"\n        },\n        {\n          "criterion": "代码规范",\n          "points": 15,\n          "description": "命名规范，注释清晰，资源正确释放"\n        }\n      ],
      "hints": [\n        "信号量 empty 表示空槽数量，初始值为缓冲区大小",\n        "信号量 full 表示满槽数量，初始值为 0",\n        "信号量 mutex 用于互斥访问缓冲区，初始值为 1",\n        "先 wait 资源信号量，再 wait 互斥信号量",\n        "post 的顺序与 wait 相反"\n      ],
      "knowledge_points": ["信号量", "进程同步", "生产者消费者", "环形缓冲区"],
      "estimated_time": 1800,
      "tags": ["编程题", "同步问题", "实践应用"]
    }
  ]
}
```

## Notes

- 生成的题目必须保证答案正确性
- 选择题的干扰项要有一定迷惑性，但不能过于刁钻
- 个性化题目要真正针对学生薄弱点，不是随机出题
- 编程题要提供充分的测试用例
- 难度评估要准确，避免难度标注与实际不符
- 每道题目都要有详细的答案解析
- 生成的题目要避免与题库现有题目重复
- 定期分析生成题目的质量，优化 Prompt
