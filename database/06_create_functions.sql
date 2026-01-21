-- =============================================
-- Vault 函数创建脚本
-- 描述: 创建常用工具函数
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

DELIMITER $$

-- =============================================
-- 1. 字符串处理函数
-- =============================================

-- 1.1 生成随机字符串
DROP FUNCTION IF EXISTS fn_generate_random_string$$
CREATE FUNCTION fn_generate_random_string(p_length INT)
RETURNS VARCHAR(255)
NOT DETERMINISTIC
NO SQL
BEGIN
    DECLARE v_result VARCHAR(255) DEFAULT '';
    DECLARE v_chars VARCHAR(62) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    DECLARE v_i INT DEFAULT 0;
    
    WHILE v_i < p_length DO
        SET v_result = CONCAT(v_result, SUBSTRING(v_chars, FLOOR(1 + RAND() * 62), 1));
        SET v_i = v_i + 1;
    END WHILE;
    
    RETURN v_result;
END$$

-- 1.2 生成分享ID
DROP FUNCTION IF EXISTS fn_generate_share_id$$
CREATE FUNCTION fn_generate_share_id()
RETURNS VARCHAR(50)
NOT DETERMINISTIC
NO SQL
BEGIN
    RETURN CONCAT('share_', UNIX_TIMESTAMP(), '_', fn_generate_random_string(8));
END$$

-- =============================================
-- 2. 计算类函数
-- =============================================

-- 2.1 计算问卷通过率
DROP FUNCTION IF EXISTS fn_calculate_pass_rate$$
CREATE FUNCTION fn_calculate_pass_rate(p_questionnaire_id BIGINT)
RETURNS DECIMAL(5,2)
READS SQL DATA
BEGIN
    DECLARE v_total_submissions INT;
    DECLARE v_pass_count INT;
    DECLARE v_pass_score DECIMAL(10,2);
    DECLARE v_pass_rate DECIMAL(5,2);
    
    -- 获取及格分数
    SELECT pass_score INTO v_pass_score
    FROM questionnaires
    WHERE id = p_questionnaire_id;
    
    -- 统计总提交数
    SELECT COUNT(*) INTO v_total_submissions
    FROM questionnaire_submissions
    WHERE questionnaire_id = p_questionnaire_id;
    
    IF v_total_submissions = 0 THEN
        RETURN 0;
    END IF;
    
    -- 统计及格人数
    SELECT COUNT(*) INTO v_pass_count
    FROM questionnaire_submissions
    WHERE questionnaire_id = p_questionnaire_id
    AND total_score >= v_pass_score;
    
    SET v_pass_rate = (v_pass_count * 100.0) / v_total_submissions;
    
    RETURN ROUND(v_pass_rate, 2);
END$$

-- 2.2 计算学生在班级中的排名
DROP FUNCTION IF EXISTS fn_get_student_class_rank$$
CREATE FUNCTION fn_get_student_class_rank(p_student_id BIGINT)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_rank INT;
    DECLARE v_grade VARCHAR(20);
    DECLARE v_class_name VARCHAR(50);
    DECLARE v_total_scores DECIMAL(10,2);
    
    -- 获取学生信息
    SELECT grade, class_name, total_scores
    INTO v_grade, v_class_name, v_total_scores
    FROM students
    WHERE id = p_student_id;
    
    -- 计算排名
    SELECT COUNT(*) + 1 INTO v_rank
    FROM students
    WHERE grade = v_grade
    AND class_name = v_class_name
    AND total_scores > v_total_scores;
    
    RETURN v_rank;
END$$

-- 2.3 计算学生知识点掌握度
DROP FUNCTION IF EXISTS fn_calculate_knowledge_mastery$$
CREATE FUNCTION fn_calculate_knowledge_mastery(
    p_student_id BIGINT,
    p_knowledge_point VARCHAR(100)
)
RETURNS DECIMAL(5,2)
READS SQL DATA
BEGIN
    DECLARE v_total_attempts INT;
    DECLARE v_correct_attempts INT;
    DECLARE v_mastery_rate DECIMAL(5,2);
    
    -- 统计该知识点的作答次数
    SELECT COUNT(*) INTO v_total_attempts
    FROM answers a
    INNER JOIN questions q ON a.question_id = q.id
    WHERE a.student_id = p_student_id
    AND q.knowledge_point = p_knowledge_point;
    
    IF v_total_attempts = 0 THEN
        RETURN 0;
    END IF;
    
    -- 统计正确次数
    SELECT COUNT(*) INTO v_correct_attempts
    FROM answers a
    INNER JOIN questions q ON a.question_id = q.id
    WHERE a.student_id = p_student_id
    AND q.knowledge_point = p_knowledge_point
    AND a.is_correct = TRUE;
    
    SET v_mastery_rate = (v_correct_attempts * 100.0) / v_total_attempts;
    
    RETURN ROUND(v_mastery_rate, 2);
END$$

-- =============================================
-- 3. 日期时间函数
-- =============================================

-- 3.1 判断问卷是否过期
DROP FUNCTION IF EXISTS fn_is_questionnaire_expired$$
CREATE FUNCTION fn_is_questionnaire_expired(p_questionnaire_id BIGINT)
RETURNS BOOLEAN
READS SQL DATA
BEGIN
    DECLARE v_deadline DATETIME;
    
    SELECT deadline INTO v_deadline
    FROM questionnaires
    WHERE id = p_questionnaire_id;
    
    IF v_deadline IS NULL THEN
        RETURN FALSE;
    END IF;
    
    RETURN NOW() > v_deadline;
END$$

-- 3.2 计算距离截止时间的天数
DROP FUNCTION IF EXISTS fn_days_until_deadline$$
CREATE FUNCTION fn_days_until_deadline(p_questionnaire_id BIGINT)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_deadline DATETIME;
    DECLARE v_days INT;
    
    SELECT deadline INTO v_deadline
    FROM questionnaires
    WHERE id = p_questionnaire_id;
    
    IF v_deadline IS NULL THEN
        RETURN 999999; -- 表示无限期
    END IF;
    
    SET v_days = DATEDIFF(v_deadline, NOW());
    
    RETURN v_days;
END$$

-- =============================================
-- 4. 权限检查函数
-- =============================================

-- 4.1 检查用户是否有权限访问问卷
DROP FUNCTION IF EXISTS fn_can_access_questionnaire$$
CREATE FUNCTION fn_can_access_questionnaire(
    p_user_id BIGINT,
    p_questionnaire_id BIGINT
)
RETURNS BOOLEAN
READS SQL DATA
BEGIN
    DECLARE v_role ENUM('STUDENT', 'TEACHER', 'ADMIN');
    DECLARE v_creator_id BIGINT;
    DECLARE v_student_id BIGINT;
    DECLARE v_target_students JSON;
    
    -- 获取用户角色
    SELECT role INTO v_role
    FROM users
    WHERE id = p_user_id;
    
    -- 管理员和教师都可以访问
    IF v_role IN ('ADMIN', 'TEACHER') THEN
        RETURN TRUE;
    END IF;
    
    -- 获取问卷创建者
    SELECT creator_id, target_students INTO v_creator_id, v_target_students
    FROM questionnaires
    WHERE id = p_questionnaire_id;
    
    -- 获取学生ID
    SELECT id INTO v_student_id
    FROM students
    WHERE user_id = p_user_id;
    
    IF v_student_id IS NULL THEN
        RETURN FALSE;
    END IF;
    
    -- 如果target_students为空，所有学生都可以访问
    IF v_target_students IS NULL THEN
        RETURN TRUE;
    END IF;
    
    -- 检查学生是否在目标列表中
    RETURN JSON_CONTAINS(v_target_students, CAST(v_student_id AS JSON));
END$$

-- 4.2 检查用户是否有权限访问聊天
DROP FUNCTION IF EXISTS fn_can_access_chat$$
CREATE FUNCTION fn_can_access_chat(
    p_user_id BIGINT,
    p_chat_id BIGINT
)
RETURNS BOOLEAN
READS SQL DATA
BEGIN
    DECLARE v_chat_user_id BIGINT;
    DECLARE v_shared BOOLEAN;
    DECLARE v_role ENUM('STUDENT', 'TEACHER', 'ADMIN');
    
    -- 获取聊天所属用户
    SELECT user_id, shared INTO v_chat_user_id, v_shared
    FROM chats
    WHERE id = p_chat_id;
    
    -- 如果是自己的聊天，可以访问
    IF v_chat_user_id = p_user_id THEN
        RETURN TRUE;
    END IF;
    
    -- 如果聊天被分享，可以访问
    IF v_shared = TRUE THEN
        RETURN TRUE;
    END IF;
    
    -- 管理员可以访问所有聊天
    SELECT role INTO v_role
    FROM users
    WHERE id = p_user_id;
    
    IF v_role = 'ADMIN' THEN
        RETURN TRUE;
    END IF;
    
    RETURN FALSE;
END$$

-- =============================================
-- 5. 统计分析函数
-- =============================================

-- 5.1 获取学生活跃度评分
DROP FUNCTION IF EXISTS fn_get_student_activity_score$$
CREATE FUNCTION fn_get_student_activity_score(p_student_id BIGINT)
RETURNS DECIMAL(5,2)
READS SQL DATA
BEGIN
    DECLARE v_question_count INT;
    DECLARE v_submission_count INT;
    DECLARE v_days_active INT;
    DECLARE v_activity_score DECIMAL(5,2);
    
    -- 获取提问数量
    SELECT total_questions INTO v_question_count
    FROM students
    WHERE id = p_student_id;
    
    -- 获取问卷提交数量
    SELECT COUNT(*) INTO v_submission_count
    FROM questionnaire_submissions
    WHERE student_id = p_student_id;
    
    -- 获取活跃天数
    SELECT COUNT(DISTINCT DATE(ol.create_time)) INTO v_days_active
    FROM operation_logs ol
    INNER JOIN students s ON ol.user_id = s.user_id
    WHERE s.id = p_student_id
    AND ol.create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY);
    
    -- 计算活跃度评分（简单的加权计算）
    SET v_activity_score = (v_question_count * 0.3) + (v_submission_count * 5) + (v_days_active * 2);
    
    -- 归一化到0-100
    IF v_activity_score > 100 THEN
        SET v_activity_score = 100;
    END IF;
    
    RETURN ROUND(v_activity_score, 2);
END$$

-- 5.2 计算题目区分度
DROP FUNCTION IF EXISTS fn_calculate_question_discrimination$$
CREATE FUNCTION fn_calculate_question_discrimination(p_question_id BIGINT)
RETURNS DECIMAL(5,2)
READS SQL DATA
BEGIN
    DECLARE v_high_group_correct DECIMAL(5,2);
    DECLARE v_low_group_correct DECIMAL(5,2);
    DECLARE v_discrimination DECIMAL(5,2);
    DECLARE v_total_students INT;
    DECLARE v_group_size INT;
    
    -- 获取答题总人数
    SELECT COUNT(DISTINCT student_id) INTO v_total_students
    FROM answers
    WHERE question_id = p_question_id;
    
    IF v_total_students < 10 THEN
        RETURN 0; -- 样本太小，无法计算
    END IF;
    
    SET v_group_size = FLOOR(v_total_students * 0.27); -- 取前27%和后27%
    
    -- 计算高分组正确率
    SELECT AVG(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) INTO v_high_group_correct
    FROM answers a
    INNER JOIN (
        SELECT student_id
        FROM questionnaire_submissions qs
        INNER JOIN answers a2 ON qs.questionnaire_id = a2.questionnaire_id AND qs.student_id = a2.student_id
        WHERE a2.question_id = p_question_id
        ORDER BY qs.total_score DESC
        LIMIT v_group_size
    ) high_group ON a.student_id = high_group.student_id
    WHERE a.question_id = p_question_id;
    
    -- 计算低分组正确率
    SELECT AVG(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) INTO v_low_group_correct
    FROM answers a
    INNER JOIN (
        SELECT student_id
        FROM questionnaire_submissions qs
        INNER JOIN answers a2 ON qs.questionnaire_id = a2.questionnaire_id AND qs.student_id = a2.student_id
        WHERE a2.question_id = p_question_id
        ORDER BY qs.total_score ASC
        LIMIT v_group_size
    ) low_group ON a.student_id = low_group.student_id
    WHERE a.question_id = p_question_id;
    
    -- 计算区分度
    SET v_discrimination = v_high_group_correct - v_low_group_correct;
    
    RETURN ROUND(v_discrimination, 2);
END$$

-- =============================================
-- 6. JSON处理函数
-- =============================================

-- 6.1 检查学生是否在目标列表中
DROP FUNCTION IF EXISTS fn_is_student_in_target$$
CREATE FUNCTION fn_is_student_in_target(
    p_student_id BIGINT,
    p_target_json JSON
)
RETURNS BOOLEAN
NO SQL
BEGIN
    IF p_target_json IS NULL THEN
        RETURN TRUE; -- NULL表示所有学生
    END IF;
    
    RETURN JSON_CONTAINS(p_target_json, CAST(p_student_id AS JSON));
END$$

DELIMITER ;

SELECT 'All functions created successfully!' AS message;
