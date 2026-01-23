-- 简化的测试数据插入脚本
USE vault;

-- 清理旧数据（可选）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE notifications;
TRUNCATE TABLE messages;
TRUNCATE TABLE chats;
TRUNCATE TABLE answers;
TRUNCATE TABLE questions;
TRUNCATE TABLE questionnaires;
TRUNCATE TABLE materials;
TRUNCATE TABLE skills;
TRUNCATE TABLE students;
TRUNCATE TABLE teachers;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 教师账号
-- 密码: teacher123
INSERT INTO users (username, password, email, nickname, role, enabled)
VALUES 
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'teacher001@vault.com', '张老师', 'TEACHER', 1),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'teacher002@vault.com', '李老师', 'TEACHER', 1),
('teacher003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'teacher003@vault.com', '王老师', 'TEACHER', 1),
('teacher004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'teacher004@vault.com', '刘老师', 'TEACHER', 1);

-- 教师详细信息
INSERT INTO teachers (user_id, teacher_number, department, title)
SELECT id, 'T2024001', '计算机系', '副教授' FROM users WHERE username = 'teacher001';

INSERT INTO teachers (user_id, teacher_number, department, title)
SELECT id, 'T2024002', '计算机系', '讲师' FROM users WHERE username = 'teacher002';

INSERT INTO teachers (user_id, teacher_number, department, title)
SELECT id, 'T2024003', '软件工程系', '教授' FROM users WHERE username = 'teacher003';

INSERT INTO teachers (user_id, teacher_number, department, title)
SELECT id, 'T2024004', '软件工程系', '助教' FROM users WHERE username = 'teacher004';

-- 2. 学生账号
-- 密码: student123
INSERT INTO users (username, password, email, nickname, role, enabled)
VALUES 
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu001@vault.com', '张三', 'STUDENT', 1),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu002@vault.com', '李四', 'STUDENT', 1),
('student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu003@vault.com', '王五', 'STUDENT', 1),
('student004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu004@vault.com', '赵六', 'STUDENT', 1),
('student005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu005@vault.com', '孙七', 'STUDENT', 1),
('student006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu006@vault.com', '周八', 'STUDENT', 1),
('student007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu007@vault.com', '吴九', 'STUDENT', 1),
('student008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu008@vault.com', '郑十', 'STUDENT', 1),
('student009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu009@vault.com', '钱一', 'STUDENT', 1),
('student010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu010@vault.com', '陈二', 'STUDENT', 1),
('student011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu011@vault.com', '冯三', 'STUDENT', 1),
('student012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu012@vault.com', '褚四', 'STUDENT', 1),
('student013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu013@vault.com', '卫五', 'STUDENT', 1),
('student014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu014@vault.com', '蒋六', 'STUDENT', 1),
('student015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu015@vault.com', '沈七', 'STUDENT', 1),
('student016', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu016@vault.com', '韩八', 'STUDENT', 1),
('student017', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu017@vault.com', '杨九', 'STUDENT', 1),
('student018', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu018@vault.com', '朱十', 'STUDENT', 1),
('student019', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu019@vault.com', '秦十一', 'STUDENT', 1),
('student020', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lXXMXMjbw3H/0qLiO', 'stu020@vault.com', '尤十二', 'STUDENT', 1);

-- 学生详细信息
INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024001', '计算机科学与技术', '2024级', '1班' FROM users WHERE username = 'student001';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024002', '计算机科学与技术', '2024级', '1班' FROM users WHERE username = 'student002';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024003', '计算机科学与技术', '2024级', '1班' FROM users WHERE username = 'student003';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024004', '计算机科学与技术', '2024级', '2班' FROM users WHERE username = 'student004';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024005', '软件工程', '2024级', '1班' FROM users WHERE username = 'student005';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024006', '计算机科学与技术', '2024级', '1班' FROM users WHERE username = 'student006';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024007', '计算机科学与技术', '2024级', '1班' FROM users WHERE username = 'student007';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024008', '计算机科学与技术', '2024级', '2班' FROM users WHERE username = 'student008';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024009', '计算机科学与技术', '2024级', '2班' FROM users WHERE username = 'student009';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024010', '计算机科学与技术', '2024级', '2班' FROM users WHERE username = 'student010';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024011', '软件工程', '2024级', '1班' FROM users WHERE username = 'student011';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024012', '软件工程', '2024级', '1班' FROM users WHERE username = 'student012';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024013', '软件工程', '2024级', '2班' FROM users WHERE username = 'student013';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024014', '软件工程', '2024级', '2班' FROM users WHERE username = 'student014';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024015', '软件工程', '2024级', '2班' FROM users WHERE username = 'student015';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024016', '数据科学', '2024级', '1班' FROM users WHERE username = 'student016';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024017', '数据科学', '2024级', '1班' FROM users WHERE username = 'student017';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024018', '数据科学', '2024级', '1班' FROM users WHERE username = 'student018';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024019', '人工智能', '2024级', '1班' FROM users WHERE username = 'student019';

INSERT INTO students (user_id, student_number, major, grade, class_name)
SELECT id, '2024020', '人工智能', '2024级', '1班' FROM users WHERE username = 'student020';

-- 3. 学习资料
INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '操作系统教材',
    '操作系统基础教材',
    'TEXTBOOK',
    '/materials/os_textbook.pdf',
    10485760,
    'application/pdf',
    id,
    1,
    JSON_ARRAY('进程管理', '内存管理', '文件系统')
FROM users WHERE username = 'teacher001';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '第一章讲义',
    '操作系统概述',
    'LECTURE',
    '/materials/chapter1.pdf',
    2097152,
    'application/pdf',
    id,
    1,
    JSON_ARRAY('操作系统概述')
FROM users WHERE username = 'teacher001';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '进程调度算法详解',
    '详细讲解FCFS、SJF、优先级等调度算法',
    'VIDEO',
    '/materials/scheduling.mp4',
    52428800,
    'video/mp4',
    id,
    1,
    JSON_ARRAY('进程调度', '调度算法')
FROM users WHERE username = 'teacher001';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '内存管理PPT',
    '内存分配、页式存储等',
    'LECTURE',
    '/materials/memory.pptx',
    5242880,
    'application/vnd.ms-powerpoint',
    id,
    1,
    JSON_ARRAY('内存管理', '页式存储', '虚拟内存')
FROM users WHERE username = 'teacher001';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '文件系统实验指导',
    '文件系统实验手册',
    'EXAM',
    '/materials/file_system_lab.pdf',
    3145728,
    'application/pdf',
    id,
    1,
    JSON_ARRAY('文件系统', '实验')
FROM users WHERE username = 'teacher002';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '数据结构与算法',
    '基础数据结构教材',
    'TEXTBOOK',
    '/materials/data_structure.pdf',
    15728640,
    'application/pdf',
    id,
    1,
    JSON_ARRAY('数据结构', '算法')
FROM users WHERE username = 'teacher003';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '软件工程导论',
    '软件开发生命周期',
    'LECTURE',
    '/materials/software_engineering.pdf',
    4194304,
    'application/pdf',
    id,
    1,
    JSON_ARRAY('软件工程', '开发流程')
FROM users WHERE username = 'teacher003';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '数据库系统原理',
    'SQL语言与关系数据库',
    'TEXTBOOK',
    '/materials/database.pdf',
    12582912,
    'application/pdf',
    id,
    1,
    JSON_ARRAY('数据库', 'SQL', '关系模型')
FROM users WHERE username = 'teacher004';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    '计算机网络协议',
    'TCP/IP协议栈详解',
    'VIDEO',
    '/materials/network.mp4',
    104857600,
    'video/mp4',
    id,
    1,
    JSON_ARRAY('计算机网络', 'TCP/IP', '协议')
FROM users WHERE username = 'teacher002';

INSERT INTO materials (title, description, type, file_url, file_size, file_type, uploader_id, is_public, knowledge_points)
SELECT 
    'Python编程基础',
    'Python语法与实践',
    'OTHER',
    '/materials/python_tutorial.ipynb',
    1048576,
    'application/x-ipynb+json',
    id,
    1,
    JSON_ARRAY('Python', '编程语言')
FROM users WHERE username = 'teacher004';

-- 4. 问卷
INSERT INTO questionnaires (title, description, creator_id, time_limit, total_score, pass_score, start_time, deadline, status)
SELECT 
    '第一章测验',
    '操作系统概述测试',
    id,
    30,
    100.00,
    60.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'PUBLISHED'
FROM users WHERE username = 'teacher001';

SET @questionnaire_id_1 = LAST_INSERT_ID();

-- 问卷1题目
INSERT INTO questions (questionnaire_id, question_order, type, content, options, correct_answer, knowledge_point, difficulty, score, explanation)
VALUES 
(
    @questionnaire_id_1,
    1,
    'SINGLE_CHOICE',
    '操作系统的主要功能不包括？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '进程管理'),
        JSON_OBJECT('key', 'B', 'value', '内存管理'),
        JSON_OBJECT('key', 'C', 'value', '文件管理'),
        JSON_OBJECT('key', 'D', 'value', '硬件制造')
    ),
    'D',
    '操作系统功能',
    'EASY',
    25.00,
    '操作系统不负责硬件制造'
),
(
    @questionnaire_id_1,
    2,
    'SINGLE_CHOICE',
    '哪种调度算法可能导致饥饿？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', 'FCFS'),
        JSON_OBJECT('key', 'B', 'value', '优先级调度'),
        JSON_OBJECT('key', 'C', 'value', '时间片轮转'),
        JSON_OBJECT('key', 'D', 'value', 'SJF')
    ),
    'B',
    '进程调度',
    'MEDIUM',
    25.00,
    '优先级调度可能使低优先级进程饥饿'
),
(
    @questionnaire_id_1,
    3,
    'TRUE_FALSE',
    '进程是资源分配和调度的基本单位',
    JSON_ARRAY(
        JSON_OBJECT('key', 'T', 'value', '正确'),
        JSON_OBJECT('key', 'F', 'value', '错误')
    ),
    'T',
    '进程管理',
    'EASY',
    25.00,
    '这是进程的定义'
),
(
    @questionnaire_id_1,
    4,
    'MULTIPLE_CHOICE',
    '操作系统的主要组成部分？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '内核'),
        JSON_OBJECT('key', 'B', 'value', '系统调用'),
        JSON_OBJECT('key', 'C', 'value', '应用程序'),
        JSON_OBJECT('key', 'D', 'value', '驱动程序')
    ),
    'A,B,D',
    '操作系统概述',
    'MEDIUM',
    25.00,
    '应用程序不属于操作系统'
);

-- 问卷2：进程管理测验
INSERT INTO questionnaires (title, description, creator_id, time_limit, total_score, pass_score, start_time, deadline, status)
SELECT 
    '第二章测验 - 进程管理',
    '测试进程与线程相关知识',
    id,
    45,
    100.00,
    60.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 10 DAY),
    'PUBLISHED'
FROM users WHERE username = 'teacher001';

SET @questionnaire_id_2 = LAST_INSERT_ID();

INSERT INTO questions (questionnaire_id, question_order, type, content, options, correct_answer, knowledge_point, difficulty, score, explanation)
VALUES 
(
    @questionnaire_id_2,
    1,
    'SINGLE_CHOICE',
    '进程和程序的主要区别是？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '进程是静态的，程序是动态的'),
        JSON_OBJECT('key', 'B', 'value', '进程是动态的，程序是静态的'),
        JSON_OBJECT('key', 'C', 'value', '进程和程序没有区别'),
        JSON_OBJECT('key', 'D', 'value', '进程比程序运行更快')
    ),
    'B',
    '进程概念',
    'EASY',
    20.00,
    '进程是程序的动态执行过程'
),
(
    @questionnaire_id_2,
    2,
    'MULTIPLE_CHOICE',
    '进程的基本状态包括？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '就绪态'),
        JSON_OBJECT('key', 'B', 'value', '运行态'),
        JSON_OBJECT('key', 'C', 'value', '阻塞态'),
        JSON_OBJECT('key', 'D', 'value', '等待态')
    ),
    'A,B,C',
    '进程状态',
    'MEDIUM',
    20.00,
    '三态模型：就绪、运行、阻塞'
),
(
    @questionnaire_id_2,
    3,
    'TRUE_FALSE',
    '线程是比进程更小的执行单位',
    JSON_ARRAY(
        JSON_OBJECT('key', 'T', 'value', '正确'),
        JSON_OBJECT('key', 'F', 'value', '错误')
    ),
    'T',
    '线程概念',
    'EASY',
    20.00,
    '线程是进程内的执行单元'
),
(
    @questionnaire_id_2,
    4,
    'SINGLE_CHOICE',
    '死锁的必要条件不包括？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '互斥'),
        JSON_OBJECT('key', 'B', 'value', '占有并等待'),
        JSON_OBJECT('key', 'C', 'value', '抢占'),
        JSON_OBJECT('key', 'D', 'value', '循环等待')
    ),
    'C',
    '死锁',
    'HARD',
    20.00,
    '不可抢占是死锁条件，抢占不是'
),
(
    @questionnaire_id_2,
    5,
    'SHORT_ANSWER',
    '简述进程间通信的几种方式',
    NULL,
    '主要包括：管道、消息队列、共享内存、信号量、套接字等',
    '进程通信',
    'MEDIUM',
    20.00,
    '需列举至少3种方式'
);

-- 问卷3：数据结构测验
INSERT INTO questionnaires (title, description, creator_id, time_limit, total_score, pass_score, start_time, deadline, status)
SELECT 
    '数据结构基础测验',
    '测试栈、队列、树等基本数据结构',
    id,
    40,
    100.00,
    70.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    'PUBLISHED'
FROM users WHERE username = 'teacher003';

SET @questionnaire_id_3 = LAST_INSERT_ID();

INSERT INTO questions (questionnaire_id, question_order, type, content, options, correct_answer, knowledge_point, difficulty, score, explanation)
VALUES 
(
    @questionnaire_id_3,
    1,
    'SINGLE_CHOICE',
    '栈的特点是？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '先进先出'),
        JSON_OBJECT('key', 'B', 'value', '后进先出'),
        JSON_OBJECT('key', 'C', 'value', '随机访问'),
        JSON_OBJECT('key', 'D', 'value', '顺序访问')
    ),
    'B',
    '栈',
    'EASY',
    20.00,
    '栈是LIFO结构'
),
(
    @questionnaire_id_3,
    2,
    'SINGLE_CHOICE',
    '二叉树的遍历方式不包括？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '前序遍历'),
        JSON_OBJECT('key', 'B', 'value', '中序遍历'),
        JSON_OBJECT('key', 'C', 'value', '后序遍历'),
        JSON_OBJECT('key', 'D', 'value', '随机遍历')
    ),
    'D',
    '二叉树',
    'EASY',
    20.00,
    '标准遍历方式有前中后序和层序'
),
(
    @questionnaire_id_3,
    3,
    'MULTIPLE_CHOICE',
    '常见的排序算法有？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '冒泡排序'),
        JSON_OBJECT('key', 'B', 'value', '快速排序'),
        JSON_OBJECT('key', 'C', 'value', '归并排序'),
        JSON_OBJECT('key', 'D', 'value', '插入排序')
    ),
    'A,B,C,D',
    '排序算法',
    'EASY',
    20.00,
    '这些都是常见排序算法'
),
(
    @questionnaire_id_3,
    4,
    'TRUE_FALSE',
    '哈希表的查找时间复杂度平均为O(1)',
    JSON_ARRAY(
        JSON_OBJECT('key', 'T', 'value', '正确'),
        JSON_OBJECT('key', 'F', 'value', '错误')
    ),
    'T',
    '哈希表',
    'MEDIUM',
    20.00,
    '理想情况下哈希表查找为O(1)'
),
(
    @questionnaire_id_3,
    5,
    'SINGLE_CHOICE',
    '平衡二叉树的特点是？',
    JSON_ARRAY(
        JSON_OBJECT('key', 'A', 'value', '任意节点左右子树高度差不超过1'),
        JSON_OBJECT('key', 'B', 'value', '完全二叉树'),
        JSON_OBJECT('key', 'C', 'value', '满二叉树'),
        JSON_OBJECT('key', 'D', 'value', '二叉搜索树')
    ),
    'A',
    '平衡树',
    'MEDIUM',
    20.00,
    'AVL树的定义'
);

-- 5. Skills
INSERT INTO skills (name, description, category, content, creator_id, enabled)
SELECT 
    '知识点推荐',
    '推荐相关学习资料',
    'CHAT',
    '根据提问推荐知识点',
    id,
    1
FROM users WHERE username = 'teacher001';

INSERT INTO skills (name, description, category, content, creator_id, enabled)
SELECT 
    '薄弱点出题',
    '针对薄弱知识点生成题目',
    'QUESTIONNAIRE',
    '分析学生薄弱点并出题',
    id,
    1
FROM users WHERE username = 'teacher001';

INSERT INTO skills (name, description, category, content, creator_id, enabled)
SELECT 
    '代码审查助手',
    '检查代码规范和潜在问题',
    'CODE_ANALYSIS',
    '分析代码质量、命名规范、注释完整性等',
    id,
    1
FROM users WHERE username = 'teacher003';

INSERT INTO skills (name, description, category, content, creator_id, enabled)
SELECT 
    '学习路径规划',
    '根据学生基础推荐学习路径',
    'CHAT',
    '评估学生当前水平，制定个性化学习计划',
    id,
    1
FROM users WHERE username = 'teacher002';

INSERT INTO skills (name, description, category, content, creator_id, enabled)
SELECT 
    '错题分析',
    '分析错题原因并给出建议',
    'QUESTIONNAIRE',
    '统计错题类型，找出知识薄弱点',
    id,
    1
FROM users WHERE username = 'teacher004';

INSERT INTO skills (name, description, category, content, creator_id, enabled)
SELECT 
    '算法可视化讲解',
    '生成算法执行过程的可视化说明',
    'CHAT',
    '将排序、搜索等算法过程可视化展示',
    id,
    1
FROM users WHERE username = 'teacher003';

-- 6. 测试对话
INSERT INTO chats (user_id, title)
SELECT user_id, '关于进程调度的问题'
FROM students WHERE student_number = '2024001';

SET @chat_id_1 = LAST_INSERT_ID();

INSERT INTO messages (chat_id, role, content, tokens)
VALUES 
(@chat_id_1, 'user', '什么是时间片轮转？', 10),
(@chat_id_1, 'assistant', '时间片轮转是一种进程调度算法，为每个进程分配固定时间片...', 50),
(@chat_id_1, 'user', '时间片多大合适？', 8),
(@chat_id_1, 'assistant', '一般10-100ms，需要权衡上下文切换开销和响应时间。', 30);

INSERT INTO chats (user_id, title)
SELECT user_id, '死锁问题求助'
FROM students WHERE student_number = '2024002';

SET @chat_id_2 = LAST_INSERT_ID();

INSERT INTO messages (chat_id, role, content, tokens)
VALUES 
(@chat_id_2, 'user', '老师，什么是死锁？', 8),
(@chat_id_2, 'assistant', '死锁是指多个进程因循环等待资源而无法继续执行的状态。需要同时满足四个条件：互斥、占有并等待、不可抢占、循环等待。', 60),
(@chat_id_2, 'user', '如何预防死锁？', 6),
(@chat_id_2, 'assistant', '可以通过破坏死锁的四个必要条件之一来预防，比如：资源有序分配（破坏循环等待）、一次性申请所有资源（破坏占有并等待）等。', 55);

INSERT INTO chats (user_id, title)
SELECT user_id, '数据结构选择'
FROM students WHERE student_number = '2024005';

SET @chat_id_3 = LAST_INSERT_ID();

INSERT INTO messages (chat_id, role, content, tokens)
VALUES 
(@chat_id_3, 'user', '什么情况下用栈，什么情况下用队列？', 12),
(@chat_id_3, 'assistant', '栈适用于后进先出的场景，如函数调用、表达式求值、括号匹配等。队列适用于先进先出的场景，如任务调度、消息队列、BFS算法等。', 70),
(@chat_id_3, 'user', '递归和栈有什么关系？', 10),
(@chat_id_3, 'assistant', '递归本质上就是利用系统调用栈。每次递归调用都会在栈中压入新的函数帧，递归返回时依次弹出。所以递归深度受栈大小限制。', 65);

INSERT INTO chats (user_id, title)
SELECT user_id, '算法复杂度分析'
FROM students WHERE student_number = '2024008';

SET @chat_id_4 = LAST_INSERT_ID();

INSERT INTO messages (chat_id, role, content, tokens)
VALUES 
(@chat_id_4, 'user', '快速排序的时间复杂度是多少？', 10),
(@chat_id_4, 'assistant', '快速排序的平均时间复杂度是O(nlogn)，最坏情况是O(n²)（当数组已排序或逆序时）。空间复杂度O(logn)（递归栈）。', 60),
(@chat_id_4, 'user', '如何避免最坏情况？', 8),
(@chat_id_4, 'assistant', '可以使用随机化选择基准元素，或使用三数取中法选择基准。这样可以大大降低最坏情况出现的概率。', 50);

-- 7. 学生答案（简化版，避免字符集冲突）
-- 学生1的答案（全对）
INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_1,
    id,
    (SELECT id FROM students WHERE student_number = '2024001'),
    'D',
    35,
    25.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_1 AND question_order = 1;

INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_1,
    id,
    (SELECT id FROM students WHERE student_number = '2024001'),
    'B',
    45,
    25.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_1 AND question_order = 2;

INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_1,
    id,
    (SELECT id FROM students WHERE student_number = '2024001'),
    'T',
    25,
    25.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_1 AND question_order = 3;

INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_1,
    id,
    (SELECT id FROM students WHERE student_number = '2024001'),
    'A,B,D',
    50,
    25.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_1 AND question_order = 4;

-- 学生2的答案（部分错误）
INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_1,
    id,
    (SELECT id FROM students WHERE student_number = '2024002'),
    'D',
    40,
    25.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_1 AND question_order = 1;

INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_1,
    id,
    (SELECT id FROM students WHERE student_number = '2024002'),
    'A',
    60,
    0,
    FALSE
FROM questions WHERE questionnaire_id = @questionnaire_id_1 AND question_order = 2;

-- 学生3的答案
INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_2,
    id,
    (SELECT id FROM students WHERE student_number = '2024003'),
    'B',
    50,
    20.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_2 AND question_order = 1;

INSERT INTO answers (questionnaire_id, question_id, student_id, answer_content, time_spent, score, is_correct)
SELECT 
    @questionnaire_id_2,
    id,
    (SELECT id FROM students WHERE student_number = '2024005'),
    'B',
    45,
    20.00,
    TRUE
FROM questions WHERE questionnaire_id = @questionnaire_id_3 AND question_order = 1;

-- 8. 通知
-- 问卷1通知
INSERT INTO notifications (user_id, title, content, type, related_id)
SELECT 
    s.user_id,
    '新问卷发布',
    '请完成《第一章测验》',
    'QUESTIONNAIRE',
    @questionnaire_id_1
FROM students s;

-- 问卷2通知
INSERT INTO notifications (user_id, title, content, type, related_id)
SELECT 
    s.user_id,
数据统计：
--------------
教师: 4位
学生: 20位
问卷: 3份（包含14道题）
资料: 10份
技能: 6个
对话: 4段
通知: 多条

测试账号：
--------------
教师:
  用户名: teacher001
  密码: teacher123
  
学生:
  用户名: student001~student020
INSERT INTO notifications (user_id, title, content, type, related_id)
SELECT 
    s.user_id,
    '新问卷发布',
    '请完成《数据结构基础测验》',
    'QUESTIONNAIRE',
    @questionnaire_id_3
FROM students s
WHERE s.major IN ('计算机科学与技术', '软件工程', '数据科学');

-- 资料更新通知
INSERT INTO notifications (user_id, title, content, type, related_id)
SELECT 
    s.user_id,
    '新资料上传',
    '老师上传了《进程调度算法详解》视频',
    'MATERIAL',
    NULL
FROM students s
LIMIT 5;

SELECT '
========================================
测试数据插入成功！
========================================

测试账号：
--------------
教师:
  用户名: teacher001
  密码: teacher123
  
学生:
  用户名: student001
  密码: student123

========================================
' AS 提示;
