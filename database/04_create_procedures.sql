-- =============================================
-- Vault 存储过程创建脚本
-- 描述: 创建常用业务逻辑存储过程
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

DELIMITER $$

-- =============================================
-- 1. 用户管理相关存储过程
-- =============================================

-- 1.1 创建学生用户
DROP PROCEDURE IF EXISTS sp_create_student$$
CREATE PROCEDURE sp_create_student(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(100),
    IN p_nickname VARCHAR(50),
    IN p_student_number VARCHAR(20),
    IN p_major VARCHAR(100),
    IN p_grade VARCHAR(20),
    IN p_class_name VARCHAR(50),
    OUT p_user_id BIGINT,
    OUT p_student_id BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_user_id = -1;
        SET p_student_id = -1;
    END;
    
    START TRANSACTION;
    
    -- 插入用户表
    INSERT INTO users (username, password, email, nickname, role)
    VALUES (p_username, p_password, p_email, p_nickname, 'STUDENT');
    
    SET p_user_id = LAST_INSERT_ID();
    
    -- 插入学生表
    INSERT INTO students (user_id, student_number, major, grade, class_name)
    VALUES (p_user_id, p_student_number, p_major, p_grade, p_class_name);
    
    SET p_student_id = LAST_INSERT_ID();
    
    COMMIT;
END$$

-- 1.2 创建教师用户
DROP PROCEDURE IF EXISTS sp_create_teacher$$
CREATE PROCEDURE sp_create_teacher(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(255),
    IN p_email VARCHAR(100),
    IN p_nickname VARCHAR(50),
    IN p_teacher_number VARCHAR(20),
    IN p_department VARCHAR(100),
    IN p_title VARCHAR(50),
    OUT p_user_id BIGINT,
    OUT p_teacher_id BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_user_id = -1;
        SET p_teacher_id = -1;
    END;
    
    START TRANSACTION;
    
    -- 插入用户表
    INSERT INTO users (username, password, email, nickname, role)
    VALUES (p_username, p_password, p_email, p_nickname, 'TEACHER');
    
    SET p_user_id = LAST_INSERT_ID();
    
    -- 插入教师表
    INSERT INTO teachers (user_id, teacher_number, department, title)
    VALUES (p_user_id, p_teacher_number, p_department, p_title);
    
    SET p_teacher_id = LAST_INSERT_ID();
    
    COMMIT;
END$$

-- =============================================
-- 2. 问卷相关存储过程
-- =============================================

-- 2.1 提交问卷
DROP PROCEDURE IF EXISTS sp_submit_questionnaire$$
CREATE PROCEDURE sp_submit_questionnaire(
    IN p_questionnaire_id BIGINT,
    IN p_student_id BIGINT,
    OUT p_total_score DECIMAL(10,2),
    OUT p_submission_id BIGINT
)
BEGIN
    DECLARE v_total_score DECIMAL(10,2) DEFAULT 0;
    DECLARE v_submit_time DATETIME;
    DECLARE v_time_spent INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_total_score = -1;
        SET p_submission_id = -1;
    END;
    
    START TRANSACTION;
    
    SET v_submit_time = NOW();
    
    -- 计算总分
    SELECT COALESCE(SUM(score), 0) INTO v_total_score
    FROM answers
    WHERE questionnaire_id = p_questionnaire_id
    AND student_id = p_student_id;
    
    -- 计算总用时
    SELECT COALESCE(SUM(time_spent), 0) INTO v_time_spent
    FROM answers
    WHERE questionnaire_id = p_questionnaire_id
    AND student_id = p_student_id;
    
    -- 插入提交记录
    INSERT INTO questionnaire_submissions (questionnaire_id, student_id, total_score, time_spent, submit_time)
    VALUES (p_questionnaire_id, p_student_id, v_total_score, v_time_spent, v_submit_time);
    
    SET p_submission_id = LAST_INSERT_ID();
    SET p_total_score = v_total_score;
    
    -- 更新学生总分
    UPDATE students
    SET total_scores = total_scores + v_total_score
    WHERE id = p_student_id;
    
    COMMIT;
END$$

-- 2.2 批量评分
DROP PROCEDURE IF EXISTS sp_batch_grade_answers$$
CREATE PROCEDURE sp_batch_grade_answers(
    IN p_questionnaire_id BIGINT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;
    
    START TRANSACTION;
    
    -- 自动评分客观题（单选、多选、判断题）
    UPDATE answers a
    INNER JOIN questions q ON a.question_id = q.id
    SET 
        a.is_correct = CASE 
            WHEN a.answer_content = q.correct_answer THEN TRUE
            ELSE FALSE
        END,
        a.score = CASE 
            WHEN a.answer_content = q.correct_answer THEN q.score
            ELSE 0
        END
    WHERE a.questionnaire_id = p_questionnaire_id
    AND q.type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE')
    AND a.is_correct IS NULL;
    
    COMMIT;
END$$

-- =============================================
-- 3. 统计分析相关存储过程
-- =============================================

-- 3.1 获取学生薄弱知识点
DROP PROCEDURE IF EXISTS sp_get_student_weak_points$$
CREATE PROCEDURE sp_get_student_weak_points(
    IN p_student_id BIGINT,
    IN p_limit INT
)
BEGIN
    SELECT 
        q.knowledge_point,
        COUNT(a.id) AS attempt_count,
        SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) AS correct_count,
        ROUND(SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id), 2) AS mastery_rate,
        AVG(a.score) AS avg_score
    FROM answers a
    INNER JOIN questions q ON a.question_id = q.id
    WHERE a.student_id = p_student_id
    AND q.knowledge_point IS NOT NULL
    GROUP BY q.knowledge_point
    HAVING mastery_rate < 60
    ORDER BY mastery_rate ASC, attempt_count DESC
    LIMIT p_limit;
END$$

-- 3.2 获取班级整体成绩统计
DROP PROCEDURE IF EXISTS sp_get_class_stats$$
CREATE PROCEDURE sp_get_class_stats(
    IN p_grade VARCHAR(20),
    IN p_class_name VARCHAR(50)
)
BEGIN
    SELECT 
        COUNT(DISTINCT s.id) AS total_students,
        AVG(s.total_scores) AS avg_score,
        MAX(s.total_scores) AS max_score,
        MIN(s.total_scores) AS min_score,
        AVG(s.total_questions) AS avg_questions,
        COUNT(DISTINCT qs.questionnaire_id) AS completed_questionnaires
    FROM students s
    LEFT JOIN questionnaire_submissions qs ON s.id = qs.student_id
    WHERE s.grade = p_grade
    AND s.class_name = p_class_name;
END$$

-- 3.3 获取问卷详细统计
DROP PROCEDURE IF EXISTS sp_get_questionnaire_detailed_stats$$
CREATE PROCEDURE sp_get_questionnaire_detailed_stats(
    IN p_questionnaire_id BIGINT
)
BEGIN
    -- 基本统计
    SELECT 
        q.id,
        q.title,
        q.status,
        q.total_score,
        COUNT(DISTINCT qs.student_id) AS submission_count,
        AVG(qs.total_score) AS avg_score,
        MAX(qs.total_score) AS max_score,
        MIN(qs.total_score) AS min_score,
        STDDEV(qs.total_score) AS score_stddev
    FROM questionnaires q
    LEFT JOIN questionnaire_submissions qs ON q.id = qs.questionnaire_id
    WHERE q.id = p_questionnaire_id
    GROUP BY q.id;
    
    -- 各题目统计
    SELECT 
        qu.id,
        qu.content,
        qu.type,
        qu.knowledge_point,
        qu.difficulty,
        COUNT(a.id) AS answer_count,
        SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) AS correct_count,
        ROUND(SUM(CASE WHEN a.is_correct = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id), 2) AS accuracy_rate,
        AVG(a.score) AS avg_score
    FROM questions qu
    LEFT JOIN answers a ON qu.id = a.question_id
    WHERE qu.questionnaire_id = p_questionnaire_id
    GROUP BY qu.id;
END$$

-- =============================================
-- 4. 数据维护相关存储过程
-- =============================================

-- 4.1 清理过期数据
DROP PROCEDURE IF EXISTS sp_clean_expired_data$$
CREATE PROCEDURE sp_clean_expired_data(
    IN p_days_to_keep INT
)
BEGIN
    DECLARE v_cutoff_date DATETIME;
    DECLARE v_deleted_logs INT DEFAULT 0;
    DECLARE v_deleted_errors INT DEFAULT 0;
    
    SET v_cutoff_date = DATE_SUB(NOW(), INTERVAL p_days_to_keep DAY);
    
    START TRANSACTION;
    
    -- 清理操作日志
    DELETE FROM operation_logs WHERE create_time < v_cutoff_date;
    SET v_deleted_logs = ROW_COUNT();
    
    -- 清理错误日志
    DELETE FROM error_logs WHERE create_time < v_cutoff_date;
    SET v_deleted_errors = ROW_COUNT();
    
    COMMIT;
    
    SELECT 
        v_deleted_logs AS deleted_operation_logs,
        v_deleted_errors AS deleted_error_logs,
        v_cutoff_date AS cutoff_date;
END$$

-- 4.2 更新学生统计信息
DROP PROCEDURE IF EXISTS sp_update_student_stats$$
CREATE PROCEDURE sp_update_student_stats(
    IN p_student_id BIGINT
)
BEGIN
    DECLARE v_total_questions INT;
    DECLARE v_total_scores DECIMAL(10,2);
    
    -- 统计提问总数
    SELECT COUNT(DISTINCT m.id) INTO v_total_questions
    FROM chats c
    INNER JOIN messages m ON c.id = m.chat_id
    INNER JOIN students s ON c.user_id = s.user_id
    WHERE s.id = p_student_id
    AND m.role = 'user';
    
    -- 统计总分数
    SELECT COALESCE(SUM(total_score), 0) INTO v_total_scores
    FROM questionnaire_submissions
    WHERE student_id = p_student_id;
    
    -- 更新学生表
    UPDATE students
    SET 
        total_questions = v_total_questions,
        total_scores = v_total_scores
    WHERE id = p_student_id;
END$$

-- 4.3 批量更新所有学生统计信息
DROP PROCEDURE IF EXISTS sp_update_all_student_stats$$
CREATE PROCEDURE sp_update_all_student_stats()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_student_id BIGINT;
    DECLARE cur CURSOR FOR SELECT id FROM students;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO v_student_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        CALL sp_update_student_stats(v_student_id);
    END LOOP;
    
    CLOSE cur;
    
    SELECT 'All student stats updated successfully!' AS message;
END$$

-- =============================================
-- 5. 代码分析相关存储过程
-- =============================================

-- 5.1 获取疑似AI使用学生列表
DROP PROCEDURE IF EXISTS sp_get_ai_suspicious_students$$
CREATE PROCEDURE sp_get_ai_suspicious_students(
    IN p_threshold DECIMAL(5,2),
    IN p_limit INT
)
BEGIN
    SELECT 
        s.id AS student_id,
        s.student_number,
        u.username,
        u.nickname,
        s.grade,
        s.class_name,
        AVG(cr.ai_detection_score) AS avg_ai_score,
        COUNT(cr.id) AS repo_count
    FROM students s
    INNER JOIN users u ON s.user_id = u.id
    INNER JOIN code_repositories cr ON s.id = cr.student_id
    GROUP BY s.id, s.student_number, u.username, u.nickname, s.grade, s.class_name
    HAVING avg_ai_score > p_threshold
    ORDER BY avg_ai_score DESC
    LIMIT p_limit;
END$$

-- 5.2 获取代码质量排行
DROP PROCEDURE IF EXISTS sp_get_code_quality_ranking$$
CREATE PROCEDURE sp_get_code_quality_ranking(
    IN p_lab_name VARCHAR(100),
    IN p_limit INT
)
BEGIN
    SELECT 
        s.id AS student_id,
        s.student_number,
        u.username,
        u.nickname,
        cr.code_quality_score,
        cr.ai_detection_score,
        cr.last_analysis_time,
        RANK() OVER (ORDER BY cr.code_quality_score DESC) AS quality_rank
    FROM code_repositories cr
    INNER JOIN students s ON cr.student_id = s.id
    INNER JOIN users u ON s.user_id = u.id
    WHERE cr.lab_name = p_lab_name
    AND cr.code_quality_score IS NOT NULL
    ORDER BY cr.code_quality_score DESC
    LIMIT p_limit;
END$$

DELIMITER ;

SELECT 'All stored procedures created successfully!' AS message;
