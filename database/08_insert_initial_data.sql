-- =============================================
-- Vault 初始化数据脚本
-- 描述: 插入测试和初始数据
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

-- =============================================
-- 1. 创建管理员账号
-- =============================================

-- 密码: admin123 (BCrypt加密后的值，实际使用时需要用BCrypt生成)
INSERT INTO users (username, password, email, nickname, role, enabled)
VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'admin@vault.com', '系统管理员', 'ADMIN', TRUE);

-- =============================================
-- 2. 创建测试教师账号
-- =============================================

-- 密码: teacher123
INSERT INTO users (username, password, email, nickname, role, enabled)
VALUES 
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'teacher001@vault.com', '张教师', 'TEACHER', TRUE),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'teacher002@vault.com', '李教师', 'TEACHER', TRUE);

-- 创建教师详细信息
INSERT INTO teachers (user_id, teacher_number, department, title)
VALUES 
((SELECT id FROM users WHERE username = 'teacher001'), 'T2024001', '计算机科学与技术系', '副教授'),
((SELECT id FROM users WHERE username = 'teacher002'), 'T2024002', '计算机科学与技术系', '讲师');

-- =============================================
-- 3. 创建测试学生账号
-- =============================================

-- 密码: student123
INSERT INTO users (username, password, email, nickname, role, enabled)
VALUES 
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'student001@vault.com', '张三', 'STUDENT', TRUE),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'student002@vault.com', '李四', 'STUDENT', TRUE),
('student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'student003@vault.com', '王五', 'STUDENT', TRUE),
('student004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'student004@vault.com', '赵六', 'STUDENT', TRUE),
('student005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'student005@vault.com', '孙七', 'STUDENT', TRUE);

-- 创建学生详细信息
INSERT INTO students (user_id, student_number, major, grade, class_name)
VALUES 
((SELECT id FROM users WHERE username = 'student001'), '2024001', '计算机科学与技术', '2024级', '1班'),
((SELECT id FROM users WHERE username = 'student002'), '2024002', '计算机科学与技术', '2024级', '1班'),
((SELECT id FROM users WHERE username = 'student003'), '2024003', '计算机科学与技术', '2024级', '1班'),
((SELECT id FROM users WHERE username = 'student004'), '2024004', '计算机科学与技术', '2024级', '2班'),
((SELECT id FROM users WHERE username = 'student005'), '2024005', '软件工程', '2024级', '1班');

-- =============================================
-- 4. 插入测试资料
-- =============================================

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
VALUES 
(
    '操作系统原理教材',
    '操作系统基础理论和实践教材，包含进程管理、内存管理、文件系统等内容',
    'TEXTBOOK',
    '/materials/os_textbook.pdf',
    10485760,
    'application/pdf',
    (SELECT id FROM users WHERE username = 'teacher001'),
    TRUE,
    JSON_ARRAY('进程管理', '内存管理', '文件系统', '进程调度')
),
(
    '第一章讲义 - 操作系统概述',
    '介绍操作系统的基本概念、发展历史和主要功能',
    'LECTURE',
    '/materials/chapter1_lecture.pdf',
    2097152,
    'application/pdf',
    (SELECT id FROM users WHERE username = 'teacher001'),
    TRUE,
    JSON_ARRAY('操作系统概述', '操作系统功能')
),
(
    '进程调度算法视频讲解',
    '详细讲解FCFS、SJF、优先级调度、时间片轮转等调度算法',
    'VIDEO',
    '/materials/scheduling_video.mp4',
    104857600,
    'video/mp4',
    (SELECT id FROM users WHERE username = 'teacher001'),
    TRUE,
    JSON_ARRAY('进程调度', '调度算法')
);

-- =============================================
-- 5. 插入测试问卷
-- =============================================

INSERT INTO questionnaires (title, description, creator_id, time_limit, total_score, pass_score, start_time, deadline, status)
VALUES 
(
    '第一章测验 - 操作系统概述',
    '测试学生对操作系统基本概念的掌握程度',
    (SELECT id FROM users WHERE username = 'teacher001'),
    30,
    100.00,
    60.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'PUBLISHED'
);

SET @questionnaire_id = LAST_INSERT_ID();

-- 插入测试题目
INSERT INTO questions (questionnaire_id, question_order, type, content, options, correct_answer, knowledge_point, difficulty, score, explanation)
VALUES 
(
    @questionnaire_id,
    1,
    'SINGLE_CHOICE',
    '操作系统的主要功能不包括以下哪一项？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '进程管理'),
        JSON_OBJECT('key', 'B', 'value', '内存管理'),
        JSON_OBJECT('key', 'C', 'value', '文件管理'),
        JSON_OBJECT('key', 'D', 'value', '硬件制造')
    ),
    'D',
    '操作系统功能',
    'EASY',
    10.00,
    '操作系统负责管理计算机硬件和软件资源，不负责硬件制造'
),
(
    @questionnaire_id,
    2,
    'SINGLE_CHOICE',
    '以下哪种调度算法可能导致饥饿现象？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', 'FCFS'),
        JSON_OBJECT('key', 'B', 'value', '优先级调度'),
        JSON_OBJECT('key', 'C', 'value', '时间片轮转'),
        JSON_OBJECT('key', 'D', 'value', 'SJF')
    ),
    'B',
    '进程调度',
    'MEDIUM',
    15.00,
    '优先级调度可能使低优先级进程长时间得不到执行，导致饥饿'
),
(
    @questionnaire_id,
    3,
    'TRUE_FALSE',
    '进程是程序的一次执行过程，是系统进行资源分配和调度的基本单位。',
    JSON_ARRAY(
        JSON_OBJECT('key', 'T', 'value', '正确'),
        JSON_OBJECT('key', 'F', 'value', '错误')
    ),
    'T',
    '进程管理',
    'EASY',
    10.00,
    '这是进程的标准定义'
),
(
    @questionnaire_id,
    4,
    'MULTIPLE_CHOICE',
    '以下哪些是操作系统的主要组成部分？（多选）',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '内核'),
        JSON_OBJECT('key', 'B', 'value', '系统调用接口'),
        JSON_OBJECT('key', 'C', 'value', '应用程序'),
        JSON_OBJECT('key', 'D', 'value', '设备驱动程序')
    ),
    'A,B,D',
    '操作系统概述',
    'MEDIUM',
    20.00,
    '操作系统由内核、系统调用接口和设备驱动程序组成，应用程序不属于操作系统'
),
(
    @questionnaire_id,
    5,
    'SHORT_ANSWER',
    '简述什么是进程调度？进程调度的目标是什么？',
    NULL,
    '进程调度是操作系统根据某种策略选择就绪队列中的进程分配CPU的过程。调度的目标包括：提高CPU利用率、提高系统吞吐量、减少响应时间、保证公平性等。',
    '进程调度',
    'MEDIUM',
    25.00,
    '需要说明进程调度的定义和主要目标'
),
(
    @questionnaire_id,
    6,
    'SHORT_ANSWER',
    '请解释时间片轮转调度算法的工作原理。',
    NULL,
    '时间片轮转算法为每个进程分配一个时间片（时间量），进程在时间片内运行。如果时间片结束进程还未完成，则被强制让出CPU，放到就绪队列末尾。下一个进程开始执行。这样循环往复，确保每个进程都能公平地获得CPU时间。',
    '进程调度',
    'HARD',
    20.00,
    '需要完整描述时间片轮转的执行过程和特点'
);

-- =============================================
-- 6. 插入测试Skills
-- =============================================

INSERT INTO skills (name, description, category, content, creator_id, enabled)
VALUES 
(
    '知识点关联推荐',
    '根据学生提问自动推荐相关知识点和学习资料',
    'CHAT',
    '# 知识点关联推荐\n\n当学生提问时：\n1. 分析问题中的关键知识点\n2. 在知识图谱中查找相关知识点\n3. 推荐相关的教材、讲义和视频\n4. 给出学习建议',
    (SELECT id FROM users WHERE username = 'teacher001'),
    TRUE
),
(
    '薄弱知识点出题',
    '根据学生的薄弱知识点生成针对性题目',
    'QUESTIONNAIRE',
    '# 薄弱知识点出题\n\n出题策略：\n1. 从学生肖像中提取掌握度低于60%的知识点\n2. 针对这些知识点生成题目\n3. 题目难度根据掌握度动态调整\n4. 优先出错误率高的知识点',
    (SELECT id FROM users WHERE username = 'teacher001'),
    TRUE
),
(
    '代码风格检查',
    '检查学生代码的规范性和风格问题',
    'CODE_ANALYSIS',
    '# 代码风格检查\n\n检查项：\n1. 命名规范\n2. 缩进和格式\n3. 注释完整性\n4. 代码复杂度\n5. 最佳实践遵循情况',
    (SELECT id FROM users WHERE username = 'teacher001'),
    TRUE
);

-- =============================================
-- 7. 插入一些测试答案
-- =============================================

-- 学生1的答案（全对）
INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent)
SELECT 
    @questionnaire_id,
    q.id,
    (SELECT id FROM students WHERE student_number = '2024001'),
    q.correct_answer,
    FLOOR(30 + RAND() * 90) -- 30-120秒随机
FROM questions q
WHERE q.questionnaire_id = @questionnaire_id
AND q.type IN ('SINGLE_CHOICE', 'TRUE_FALSE', 'MULTIPLE_CHOICE');

-- 学生2的答案（部分对）
INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent)
VALUES
(
    @questionnaire_id,
    (SELECT id FROM questions WHERE questionnaire_id = @questionnaire_id AND question_order = 1),
    (SELECT id FROM students WHERE student_number = '2024002'),
    'D',
    45
),
(
    @questionnaire_id,
    (SELECT id FROM questions WHERE questionnaire_id = @questionnaire_id AND question_order = 2),
    (SELECT id FROM students WHERE student_number = '2024002'),
    'A', -- 错误答案
    60
),
(
    @questionnaire_id,
    (SELECT id FROM questions WHERE questionnaire_id = @questionnaire_id AND question_order = 3),
    (SELECT id FROM students WHERE student_number = '2024002'),
    'T',
    20
);

-- =============================================
-- 8. 插入测试对话
-- =============================================

INSERT INTO chats (user_id, title)
VALUES 
(
    (SELECT user_id FROM students WHERE student_number = '2024001'),
    '关于进程调度的问题'
);

SET @chat_id = LAST_INSERT_ID();

INSERT INTO messages (chat_id, role, content, tokens)
VALUES 
(
    @chat_id,
    'user',
    '老师，我不太理解时间片轮转调度算法，能给我解释一下吗？',
    15
),
(
    @chat_id,
    'assistant',
    '当然可以！时间片轮转（Round Robin）是一种非常重要的进程调度算法。\n\n简单来说：\n1. 系统为每个进程分配一个固定的时间片（比如10ms）\n2. 进程按顺序排队等待执行\n3. 轮到某个进程时，它最多可以运行一个时间片的时间\n4. 时间片用完后，即使进程还没执行完，也要让出CPU，排到队尾\n5. 下一个进程开始执行\n\n这样可以保证每个进程都能公平地获得CPU时间。\n\n我给你推荐一个视频教程：《进程调度算法视频讲解》，里面有详细的动画演示。',
    120
),
(
    @chat_id,
    'user',
    '那时间片应该设置多大比较合适呢？',
    10
),
(
    @chat_id,
    'assistant',
    '这是个很好的问题！时间片的大小需要权衡：\n\n**时间片太大**：\n- 退化成FCFS（先来先服务）\n- 响应时间变长\n\n**时间片太小**：\n- 上下文切换次数增加\n- 系统开销变大\n- CPU利用率降低\n\n**一般原则**：\n- 时间片应该比进程切换时间长得多（通常是切换时间的100倍以上）\n- 常见值：10-100ms\n- 需要根据系统特性和应用场景调整\n\n建议你做一下《第一章测验》中关于进程调度的题目，可以加深理解。',
    100
);

-- =============================================
-- 9. 插入测试通知
-- =============================================

INSERT INTO notifications (user_id, title, content, type, related_id)
SELECT 
    s.user_id,
    '新问卷发布',
    '教师发布了新问卷《第一章测验 - 操作系统概述》，请在截止日期前完成',
    'QUESTIONNAIRE',
    @questionnaire_id
FROM students s;

-- =============================================
-- 10. 更新统计信息
-- =============================================

-- 更新学生统计信息
CALL sp_update_all_student_stats();

SELECT '
========================================
Initial data inserted successfully!
========================================

Test Accounts:
--------------
Admin:
  Username: admin
  Password: admin123

Teacher:
  Username: teacher001
  Password: teacher123
  
Student:
  Username: student001
  Password: student123

Note: All passwords are hashed with BCrypt
========================================
' AS message;
