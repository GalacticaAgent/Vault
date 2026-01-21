-- =============================================
-- Vault 视图创建脚本
-- 描述: 创建常用查询视图，简化复杂查询
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

-- =============================================
-- 1. 用户相关视图
-- =============================================

-- 1.1 完整用户信息视图
CREATE OR REPLACE VIEW `v_user_full_info` AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.nickname,
    u.avatar,
    u.role,
    u.enabled,
    u.last_login_time,
    u.create_time,
    u.update_time,
    CASE 
        WHEN u.role = 'STUDENT' THEN s.student_number
        WHEN u.role = 'TEACHER' THEN t.teacher_number
        ELSE NULL
    END AS number,
    CASE 
        WHEN u.role = 'STUDENT' THEN s.major
        WHEN u.role = 'TEACHER' THEN t.department
        ELSE NULL
    END AS department_or_major,
    CASE 
        WHEN u.role = 'STUDENT' THEN s.grade
        WHEN u.role = 'TEACHER' THEN t.title
        ELSE NULL
    END AS grade_or_title,
    CASE 
        WHEN u.role = 'STUDENT' THEN s.class_name
        ELSE NULL
    END AS class_name
FROM users u
LEFT JOIN students s ON u.id = s.user_id
LEFT JOIN teachers t ON u.id = t.user_id;

-- 1.2 学生排行榜视图
CREATE OR REPLACE VIEW `v_student_ranking` AS
SELECT 
    s.id AS student_id,
    u.username,
    u.nickname,
    s.student_number,
    s.grade,
    s.class_name,
    s.total_questions,
    s.total_scores,
    RANK() OVER (ORDER BY s.total_scores DESC) AS score_rank,
    RANK() OVER (ORDER BY s.total_questions DESC) AS question_rank,
    RANK() OVER (PARTITION BY s.grade, s.class_name ORDER BY s.total_scores DESC) AS class_rank
FROM students s
INNER JOIN users u ON s.user_id = u.id
WHERE u.enabled = TRUE;

-- =============================================
-- 2. 问卷相关视图
-- =============================================

-- 2.1 问卷统计视图
CREATE OR REPLACE VIEW `v_questionnaire_stats` AS
SELECT 
    q.id AS questionnaire_id,
    q.title,
    q.status,
    q.total_score,
    q.start_time,
    q.deadline,
    COUNT(DISTINCT qs.student_id) AS submission_count,
    AVG(qs.total_score) AS avg_score,
    MAX(qs.total_score) AS max_score,
    MIN(qs.total_score) AS min_score,
    COUNT(DISTINCT CASE WHEN qs.total_score >= q.pass_score THEN qs.student_id END) AS pass_count,
    COUNT(DISTINCT questions.id) AS question_count
FROM questionnaires q
LEFT JOIN questionnaire_submissions qs ON q.id = qs.questionnaire_id
LEFT JOIN questions ON q.id = questions.questionnaire_id
GROUP BY q.id, q.title, q.status, q.total_score, q.start_time, q.deadline, q.pass_score;

-- 2.2 学生问卷成绩视图
CREATE OR REPLACE VIEW `v_student_questionnaire_results` AS
SELECT 
    qs.student_id,
    s.student_number,
    u.username,
    u.nickname,
    s.grade,
    s.class_name,
    qs.questionnaire_id,
    q.title AS questionnaire_title,
    qs.total_score,
    q.total_score AS max_score,
    ROUND((qs.total_score / q.total_score) * 100, 2) AS percentage,
    CASE 
        WHEN qs.total_score >= q.pass_score THEN '及格'
        ELSE '不及格'
    END AS pass_status,
    qs.time_spent,
    qs.submit_time
FROM questionnaire_submissions qs
INNER JOIN students s ON qs.student_id = s.id
INNER JOIN users u ON s.user_id = u.id
INNER JOIN questionnaires q ON qs.questionnaire_id = q.id;

-- 2.3 题目正确率视图
CREATE OR REPLACE VIEW `v_question_accuracy` AS
SELECT 
    q.id AS question_id,
    q.questionnaire_id,
    q.content AS question_content,
    q.type,
    q.knowledge_point,
    q.difficulty,
    q.score,
    COUNT(a.id) AS total_answers,
    SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) AS correct_answers,
    CASE 
        WHEN COUNT(a.id) > 0 THEN ROUND(SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id), 2)
        ELSE NULL
    END AS accuracy_rate,
    AVG(a.score) AS avg_score,
    AVG(a.time_spent) AS avg_time_spent
FROM questions q
LEFT JOIN answers a ON q.id = a.question_id
GROUP BY q.id, q.questionnaire_id, q.content, q.type, q.knowledge_point, q.difficulty, q.score;

-- =============================================
-- 3. 聊天相关视图
-- =============================================

-- 3.1 聊天会话摘要视图
CREATE OR REPLACE VIEW `v_chat_summary` AS
SELECT 
    c.id AS chat_id,
    c.user_id,
    u.username,
    u.nickname,
    c.title,
    c.shared,
    c.share_id,
    COUNT(m.id) AS message_count,
    SUM(m.tokens) AS total_tokens,
    MIN(m.create_time) AS first_message_time,
    MAX(m.create_time) AS last_message_time,
    c.create_time,
    c.update_time
FROM chats c
INNER JOIN users u ON c.user_id = u.id
LEFT JOIN messages m ON c.id = m.chat_id
GROUP BY c.id, c.user_id, u.username, u.nickname, c.title, c.shared, c.share_id, c.create_time, c.update_time;

-- 3.2 用户提问统计视图
CREATE OR REPLACE VIEW `v_user_question_stats` AS
SELECT 
    u.id AS user_id,
    u.username,
    u.nickname,
    u.role,
    COUNT(DISTINCT c.id) AS chat_count,
    COUNT(m.id) AS question_count,
    SUM(m.tokens) AS total_tokens,
    DATE(MIN(c.create_time)) AS first_chat_date,
    DATE(MAX(c.update_time)) AS last_chat_date
FROM users u
LEFT JOIN chats c ON u.id = c.user_id
LEFT JOIN messages m ON c.id = m.chat_id AND m.role = 'user'
GROUP BY u.id, u.username, u.nickname, u.role;

-- =============================================
-- 4. 资料相关视图
-- =============================================

-- 4.1 资料统计视图
CREATE OR REPLACE VIEW `v_material_stats` AS
SELECT 
    m.id AS material_id,
    m.title,
    m.type,
    m.file_type,
    m.file_size,
    m.is_public,
    m.download_count,
    u.username AS uploader_username,
    u.nickname AS uploader_nickname,
    m.create_time,
    m.update_time
FROM materials m
INNER JOIN users u ON m.uploader_id = u.id;

-- 4.2 教师上传资料统计视图
CREATE OR REPLACE VIEW `v_teacher_material_stats` AS
SELECT 
    t.id AS teacher_id,
    t.teacher_number,
    u.username,
    u.nickname,
    COUNT(m.id) AS material_count,
    SUM(m.download_count) AS total_downloads,
    SUM(m.file_size) AS total_size
FROM teachers t
INNER JOIN users u ON t.user_id = u.id
LEFT JOIN materials m ON u.id = m.uploader_id
GROUP BY t.id, t.teacher_number, u.username, u.nickname;

-- =============================================
-- 5. 代码分析相关视图
-- =============================================

-- 5.1 学生代码质量视图
CREATE OR REPLACE VIEW `v_student_code_quality` AS
SELECT 
    s.id AS student_id,
    s.student_number,
    u.username,
    u.nickname,
    s.grade,
    s.class_name,
    COUNT(cr.id) AS repo_count,
    AVG(cr.ai_detection_score) AS avg_ai_score,
    AVG(cr.code_quality_score) AS avg_quality_score,
    COUNT(cc.id) AS total_commits,
    SUM(cc.lines_added) AS total_lines_added,
    SUM(cc.lines_deleted) AS total_lines_deleted
FROM students s
INNER JOIN users u ON s.user_id = u.id
LEFT JOIN code_repositories cr ON s.id = cr.student_id
LEFT JOIN code_commits cc ON cr.id = cc.repository_id
GROUP BY s.id, s.student_number, u.username, u.nickname, s.grade, s.class_name;

-- 5.2 实验代码提交统计视图
CREATE OR REPLACE VIEW `v_lab_submission_stats` AS
SELECT 
    cr.lab_name,
    COUNT(DISTINCT cr.student_id) AS student_count,
    AVG(cr.ai_detection_score) AS avg_ai_score,
    AVG(cr.code_quality_score) AS avg_quality_score,
    SUM(cc.lines_added) AS total_lines_added
FROM code_repositories cr
LEFT JOIN code_commits cc ON cr.id = cc.repository_id
GROUP BY cr.lab_name;

-- =============================================
-- 6. 知识点掌握视图
-- =============================================

-- 6.1 学生知识点掌握统计视图
CREATE OR REPLACE VIEW `v_student_knowledge_mastery` AS
SELECT 
    a.student_id,
    s.student_number,
    u.username,
    u.nickname,
    q.knowledge_point,
    COUNT(a.id) AS attempt_count,
    SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) AS correct_count,
    ROUND(SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id), 2) AS mastery_rate,
    AVG(a.score) AS avg_score,
    MAX(a.create_time) AS last_attempt_time
FROM answers a
INNER JOIN students s ON a.student_id = s.id
INNER JOIN users u ON s.user_id = u.id
INNER JOIN questions q ON a.question_id = q.id
WHERE q.knowledge_point IS NOT NULL
GROUP BY a.student_id, s.student_number, u.username, u.nickname, q.knowledge_point;

-- 6.2 知识点难度分析视图
CREATE OR REPLACE VIEW `v_knowledge_point_difficulty` AS
SELECT 
    q.knowledge_point,
    COUNT(DISTINCT q.id) AS question_count,
    COUNT(a.id) AS total_attempts,
    AVG(CASE 
        WHEN q.difficulty = 'EASY' THEN 1
        WHEN q.difficulty = 'MEDIUM' THEN 2
        WHEN q.difficulty = 'HARD' THEN 3
    END) AS avg_difficulty_level,
    ROUND(SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id), 2) AS overall_accuracy,
    AVG(a.time_spent) AS avg_time_spent
FROM questions q
LEFT JOIN answers a ON q.id = a.question_id
WHERE q.knowledge_point IS NOT NULL
GROUP BY q.knowledge_point;

-- =============================================
-- 7. 系统活跃度视图
-- =============================================

-- 7.1 每日活跃用户视图
CREATE OR REPLACE VIEW `v_daily_active_users` AS
SELECT 
    DATE(ol.create_time) AS activity_date,
    COUNT(DISTINCT ol.user_id) AS active_users,
    COUNT(ol.id) AS total_operations,
    SUM(CASE WHEN ol.status = 1 THEN 1 ELSE 0 END) AS successful_operations,
    SUM(CASE WHEN ol.status = 0 THEN 1 ELSE 0 END) AS failed_operations,
    AVG(ol.execution_time) AS avg_execution_time
FROM operation_logs ol
GROUP BY DATE(ol.create_time);

-- 7.2 用户活跃度统计视图
CREATE OR REPLACE VIEW `v_user_activity_stats` AS
SELECT 
    u.id AS user_id,
    u.username,
    u.nickname,
    u.role,
    COUNT(DISTINCT DATE(ol.create_time)) AS active_days,
    COUNT(ol.id) AS total_operations,
    DATE(MIN(ol.create_time)) AS first_activity,
    DATE(MAX(ol.create_time)) AS last_activity,
    DATEDIFF(CURDATE(), DATE(MAX(ol.create_time))) AS days_since_last_active
FROM users u
LEFT JOIN operation_logs ol ON u.id = ol.user_id
GROUP BY u.id, u.username, u.nickname, u.role;

-- =============================================
-- 8. 通知相关视图
-- =============================================

-- 8.1 未读通知统计视图
CREATE OR REPLACE VIEW `v_unread_notifications` AS
SELECT 
    n.user_id,
    u.username,
    COUNT(n.id) AS unread_count,
    MAX(n.create_time) AS latest_notification_time
FROM notifications n
INNER JOIN users u ON n.user_id = u.id
WHERE n.is_read = FALSE
GROUP BY n.user_id, u.username;

SELECT 'All views created successfully!' AS message;
