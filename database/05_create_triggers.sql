-- =============================================
-- Vault 触发器创建脚本
-- 描述: 创建自动化业务逻辑触发器
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

DELIMITER $$

-- =============================================
-- 1. 用户相关触发器
-- =============================================

-- 1.1 用户登录时间更新触发器（通过操作日志）
DROP TRIGGER IF EXISTS tr_update_last_login$$
CREATE TRIGGER tr_update_last_login
AFTER INSERT ON operation_logs
FOR EACH ROW
BEGIN
    IF NEW.operation = 'LOGIN' AND NEW.status = 1 AND NEW.user_id IS NOT NULL THEN
        UPDATE users 
        SET last_login_time = NEW.create_time 
        WHERE id = NEW.user_id;
    END IF;
END$$

-- =============================================
-- 2. 问卷相关触发器
-- =============================================

-- 2.1 答案插入时自动评分触发器（客观题）
DROP TRIGGER IF EXISTS tr_auto_grade_answer$$
CREATE TRIGGER tr_auto_grade_answer
BEFORE INSERT ON answers
FOR EACH ROW
BEGIN
    DECLARE v_correct_answer TEXT;
    DECLARE v_question_score DECIMAL(10,2);
    DECLARE v_question_type ENUM('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER', 'CODING');
    
    -- 获取题目信息
    SELECT correct_answer, score, type INTO v_correct_answer, v_question_score, v_question_type
    FROM questions
    WHERE id = NEW.question_id;
    
    -- 如果是客观题，自动评分
    IF v_question_type IN ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE') THEN
        IF NEW.answer_content = v_correct_answer THEN
            SET NEW.is_correct = TRUE;
            SET NEW.score = v_question_score;
        ELSE
            SET NEW.is_correct = FALSE;
            SET NEW.score = 0;
        END IF;
    END IF;
END$$

-- 2.2 问卷状态变更触发器
DROP TRIGGER IF EXISTS tr_questionnaire_status_change$$
CREATE TRIGGER tr_questionnaire_status_change
AFTER UPDATE ON questionnaires
FOR EACH ROW
BEGIN
    -- 如果问卷从DRAFT变为PUBLISHED，通知相关学生
    IF OLD.status = 'DRAFT' AND NEW.status = 'PUBLISHED' THEN
        INSERT INTO notifications (user_id, title, content, type, related_id)
        SELECT 
            s.user_id,
            CONCAT('新问卷: ', NEW.title),
            CONCAT('教师发布了新的问卷，截止时间: ', DATE_FORMAT(NEW.deadline, '%Y-%m-%d %H:%i')),
            'QUESTIONNAIRE',
            NEW.id
        FROM students s
        WHERE NEW.target_students IS NULL 
        OR JSON_CONTAINS(NEW.target_students, CAST(s.id AS JSON));
    END IF;
END$$

-- =============================================
-- 3. 资料相关触发器
-- =============================================

-- 3.1 资料下载计数触发器（通过操作日志）
DROP TRIGGER IF EXISTS tr_increment_download_count$$
CREATE TRIGGER tr_increment_download_count
AFTER INSERT ON operation_logs
FOR EACH ROW
BEGIN
    DECLARE v_material_id BIGINT;
    
    IF NEW.operation = 'DOWNLOAD_MATERIAL' AND NEW.status = 1 THEN
        -- 从参数中提取material_id
        SET v_material_id = JSON_UNQUOTE(JSON_EXTRACT(NEW.params, '$.materialId'));
        
        IF v_material_id IS NOT NULL THEN
            UPDATE materials 
            SET download_count = download_count + 1 
            WHERE id = CAST(v_material_id AS UNSIGNED);
        END IF;
    END IF;
END$$

-- 3.2 新资料上传通知触发器
DROP TRIGGER IF EXISTS tr_material_upload_notification$$
CREATE TRIGGER tr_material_upload_notification
AFTER INSERT ON materials
FOR EACH ROW
BEGIN
    -- 如果是公开资料，通知所有学生
    IF NEW.is_public = TRUE THEN
        INSERT INTO notifications (user_id, title, content, type, related_id)
        SELECT 
            s.user_id,
            CONCAT('新资料: ', NEW.title),
            CONCAT('教师上传了新的学习资料'),
            'MATERIAL',
            NEW.id
        FROM students s;
    -- 如果是针对特定班级，只通知该班级学生
    ELSEIF NEW.target_classes IS NOT NULL THEN
        INSERT INTO notifications (user_id, title, content, type, related_id)
        SELECT 
            s.user_id,
            CONCAT('新资料: ', NEW.title),
            CONCAT('教师上传了新的学习资料'),
            'MATERIAL',
            NEW.id
        FROM students s
        WHERE JSON_CONTAINS(NEW.target_classes, JSON_QUOTE(CONCAT(s.grade, '-', s.class_name)));
    END IF;
END$$

-- =============================================
-- 4. 聊天相关触发器
-- =============================================

-- 4.1 消息插入时更新对话时间触发器
DROP TRIGGER IF EXISTS tr_update_chat_time$$
CREATE TRIGGER tr_update_chat_time
AFTER INSERT ON messages
FOR EACH ROW
BEGIN
    UPDATE chats 
    SET update_time = NEW.create_time 
    WHERE id = NEW.chat_id;
END$$

-- 4.2 学生提问计数触发器
DROP TRIGGER IF EXISTS tr_increment_question_count$$
CREATE TRIGGER tr_increment_question_count
AFTER INSERT ON messages
FOR EACH ROW
BEGIN
    DECLARE v_student_id BIGINT;
    
    -- 如果消息是用户发送的
    IF NEW.role = 'user' THEN
        -- 查找对应的学生ID
        SELECT s.id INTO v_student_id
        FROM chats c
        INNER JOIN students s ON c.user_id = s.user_id
        WHERE c.id = NEW.chat_id;
        
        -- 更新学生提问总数
        IF v_student_id IS NOT NULL THEN
            UPDATE students 
            SET total_questions = total_questions + 1 
            WHERE id = v_student_id;
        END IF;
    END IF;
END$$

-- =============================================
-- 5. 代码仓库相关触发器
-- =============================================

-- 5.1 代码提交后更新仓库信息触发器
DROP TRIGGER IF EXISTS tr_update_repo_on_commit$$
CREATE TRIGGER tr_update_repo_on_commit
AFTER INSERT ON code_commits
FOR EACH ROW
BEGIN
    UPDATE code_repositories
    SET 
        last_commit_hash = NEW.commit_hash,
        update_time = NOW()
    WHERE id = NEW.repository_id
    AND (last_commit_hash IS NULL OR NEW.commit_time > 
        (SELECT commit_time FROM code_commits WHERE repository_id = NEW.repository_id AND commit_hash = last_commit_hash LIMIT 1));
END$$

-- =============================================
-- 6. Skills 相关触发器
-- =============================================

-- 6.1 Skills 使用计数触发器
DROP TRIGGER IF EXISTS tr_increment_skill_usage$$
CREATE TRIGGER tr_increment_skill_usage
AFTER INSERT ON messages
FOR EACH ROW
BEGIN
    DECLARE v_skill_id BIGINT;
    DECLARE v_skills_json JSON;
    DECLARE v_skill_count INT;
    DECLARE v_index INT DEFAULT 0;
    
    SET v_skills_json = NEW.skills;
    
    IF v_skills_json IS NOT NULL THEN
        SET v_skill_count = JSON_LENGTH(v_skills_json);
        
        WHILE v_index < v_skill_count DO
            SET v_skill_id = JSON_UNQUOTE(JSON_EXTRACT(v_skills_json, CONCAT('$[', v_index, '].skillId')));
            
            IF v_skill_id IS NOT NULL THEN
                UPDATE skills 
                SET usage_count = usage_count + 1 
                WHERE id = CAST(v_skill_id AS UNSIGNED);
            END IF;
            
            SET v_index = v_index + 1;
        END WHILE;
    END IF;
END$$

-- =============================================
-- 7. 通知相关触发器
-- =============================================

-- 7.1 新通知提醒触发器（可用于后续推送服务）
DROP TRIGGER IF EXISTS tr_new_notification_alert$$
CREATE TRIGGER tr_new_notification_alert
AFTER INSERT ON notifications
FOR EACH ROW
BEGIN
    -- 这里可以插入到消息队列表，用于异步推送
    -- 目前仅记录日志
    INSERT INTO operation_logs (user_id, operation, params, status)
    VALUES (
        NEW.user_id,
        'NEW_NOTIFICATION',
        JSON_OBJECT('notificationId', NEW.id, 'title', NEW.title),
        1
    );
END$$

-- =============================================
-- 8. 数据完整性触发器
-- =============================================

-- 8.1 删除用户前检查触发器
DROP TRIGGER IF EXISTS tr_before_delete_user$$
CREATE TRIGGER tr_before_delete_user
BEFORE DELETE ON users
FOR EACH ROW
BEGIN
    -- 记录删除操作
    INSERT INTO operation_logs (user_id, operation, params, status)
    VALUES (
        OLD.id,
        'DELETE_USER',
        JSON_OBJECT('username', OLD.username, 'role', OLD.role),
        1
    );
END$$

-- 8.2 问卷提交完成性检查触发器
DROP TRIGGER IF EXISTS tr_check_submission_completeness$$
CREATE TRIGGER tr_check_submission_completeness
BEFORE INSERT ON questionnaire_submissions
FOR EACH ROW
BEGIN
    DECLARE v_total_questions INT;
    DECLARE v_answered_questions INT;
    
    -- 统计问卷总题数
    SELECT COUNT(*) INTO v_total_questions
    FROM questions
    WHERE questionnaire_id = NEW.questionnaire_id;
    
    -- 统计学生已答题数
    SELECT COUNT(*) INTO v_answered_questions
    FROM answers
    WHERE questionnaire_id = NEW.questionnaire_id
    AND student_id = NEW.student_id;
    
    -- 如果答题不完整，抛出错误
    IF v_answered_questions < v_total_questions THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = '问卷未完成，不能提交';
    END IF;
END$$

DELIMITER ;

SELECT 'All triggers created successfully!' AS message;
