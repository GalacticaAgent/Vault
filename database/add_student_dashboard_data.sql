-- =============================================
-- 添加学生看板测试数据
-- 为student001 (张三) 添加学习数据
-- =============================================

USE vault;

-- 更新student001的学习统计数据
UPDATE students 
SET total_questions = 15, total_scores = 85.5
WHERE student_number = '2024001';

-- 删除已有的测试对话（如果存在）
DELETE c FROM chats c
JOIN users u ON c.user_id = u.id
WHERE u.username = 'student001' AND c.title LIKE '操作系统%';

-- 为student001创建一些聊天对话（增加近期活动）
INSERT INTO chats (user_id, title, create_time, update_time)
SELECT 
    u.id,
    '操作系统进程调度问题讨论',
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 1 DAY)
FROM users u
WHERE u.username = 'student001';

INSERT INTO chats (user_id, title, create_time, update_time)
SELECT 
    u.id,
    '内存管理算法学习笔记',
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM users u
WHERE u.username = 'student001';

INSERT INTO chats (user_id, title, create_time, update_time)
SELECT 
    u.id,
    '文件系统实现细节咨询',
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    DATE_SUB(NOW(), INTERVAL 3 DAY)
FROM users u
WHERE u.username = 'student001';

-- 验证数据插入
SELECT '=== 学生基本信息 ===' AS info;
SELECT 
    s.student_number,
    u.nickname,
    s.total_questions,
    s.total_scores
FROM students s
JOIN users u ON s.user_id = u.id
WHERE s.student_number = '2024001';

SELECT '=== 最近对话 ===' AS info;
SELECT 
    c.id,
    c.title,
    c.create_time
FROM chats c
JOIN users u ON c.user_id = u.id
WHERE u.username = 'student001'
ORDER BY c.create_time DESC
LIMIT 5;

