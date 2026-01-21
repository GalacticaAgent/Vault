-- =============================================
-- Vault 索引优化脚本
-- 描述: 创建额外的性能优化索引
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

-- =============================================
-- 1. 复合索引优化
-- =============================================

-- 1.1 学生表复合索引
ALTER TABLE students
ADD INDEX idx_grade_class_score (grade, class_name, total_scores);

-- 1.2 问卷提交表复合索引
ALTER TABLE questionnaire_submissions
ADD INDEX idx_quest_student_score (questionnaire_id, student_id, total_score);

-- 1.3 答案表复合索引
ALTER TABLE answers
ADD INDEX idx_student_correct (student_id, is_correct),
ADD INDEX idx_quest_correct (questionnaire_id, is_correct);

-- 1.4 消息表复合索引
ALTER TABLE messages
ADD INDEX idx_chat_role (chat_id, role),
ADD INDEX idx_chat_role_time (chat_id, role, create_time);

-- 1.5 题目表复合索引
ALTER TABLE questions
ADD INDEX idx_quest_order (questionnaire_id, question_order),
ADD INDEX idx_kp_difficulty (knowledge_point, difficulty);

-- =============================================
-- 2. 覆盖索引优化
-- =============================================

-- 2.1 用户快速查询索引
ALTER TABLE users
ADD INDEX idx_username_enabled (username, enabled),
ADD INDEX idx_email_enabled (email, enabled);

-- 2.2 学生快速查询索引
ALTER TABLE students
ADD INDEX idx_student_num_userid (student_number, user_id);

-- 2.3 教师快速查询索引
ALTER TABLE teachers
ADD INDEX idx_teacher_num_userid (teacher_number, user_id);

-- =============================================
-- 3. 查询性能优化索引
-- =============================================

-- 3.1 资料搜索优化
ALTER TABLE materials
ADD INDEX idx_type_public_time (type, is_public, create_time);

-- 3.2 代码仓库查询优化
ALTER TABLE code_repositories
ADD INDEX idx_student_lab (student_id, lab_name),
ADD INDEX idx_analysis_time (last_analysis_time);

-- 3.3 操作日志查询优化
ALTER TABLE operation_logs
ADD INDEX idx_user_operation_time (user_id, operation, create_time),
ADD INDEX idx_operation_status (operation, status);

-- 3.4 通知查询优化
ALTER TABLE notifications
ADD INDEX idx_user_read_time (user_id, is_read, create_time),
ADD INDEX idx_type_time (type, create_time);

-- =============================================
-- 4. 排序优化索引
-- =============================================

-- 4.1 聊天排序索引
ALTER TABLE chats
ADD INDEX idx_user_update (user_id, update_time DESC);

-- 4.2 问卷排序索引
ALTER TABLE questionnaires
ADD INDEX idx_status_deadline (status, deadline),
ADD INDEX idx_creator_status (creator_id, status);

-- 4.3 问卷提交排序索引
ALTER TABLE questionnaire_submissions
ADD INDEX idx_quest_score (questionnaire_id, total_score DESC);

-- =============================================
-- 5. JSON字段索引优化（MySQL 8.0+）
-- =============================================

-- 5.1 问卷目标学生虚拟列索引
ALTER TABLE questionnaires
ADD COLUMN target_student_count INT GENERATED ALWAYS AS (JSON_LENGTH(target_students)) STORED,
ADD INDEX idx_target_student_count (target_student_count);

-- 5.2 资料知识点虚拟列索引
ALTER TABLE materials
ADD COLUMN knowledge_point_count INT GENERATED ALWAYS AS (JSON_LENGTH(knowledge_points)) STORED,
ADD INDEX idx_knowledge_point_count (knowledge_point_count);

-- =============================================
-- 6. 时间范围查询优化
-- =============================================

-- 6.1 创建时间范围分区（可选，适用于大数据量）
-- 注意：这需要在表创建时规划，这里仅作为示例

-- 6.2 时间索引优化
ALTER TABLE code_commits
ADD INDEX idx_repo_time (repository_id, commit_time DESC);

ALTER TABLE error_logs
ADD INDEX idx_type_time (error_type, create_time DESC);

-- =============================================
-- 7. 统计查询优化
-- =============================================

-- 7.1 Skills使用统计索引
ALTER TABLE skills
ADD INDEX idx_category_enabled_usage (category, enabled, usage_count DESC);

-- 7.2 看板卡片查询索引
ALTER TABLE dashboard_cards
ADD INDEX idx_user_order (user_id, card_order);

-- 7.3 文件查询索引
ALTER TABLE files
ADD INDEX idx_uploader_time (uploader_id, create_time DESC),
ADD INDEX idx_type_time (file_type, create_time DESC);

-- =============================================
-- 8. 外键索引检查和优化
-- =============================================

-- 所有外键字段都应该有索引（已在表创建时添加）
-- 这里再次确认关键外键索引

-- 验证索引是否存在
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS COLUMNS,
    INDEX_TYPE,
    NON_UNIQUE
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'vault'
AND TABLE_NAME IN (
    'students', 'teachers', 'chats', 'messages', 
    'questionnaires', 'questions', 'answers', 
    'questionnaire_submissions', 'materials', 
    'code_repositories', 'code_commits', 'skills',
    'dashboard_cards', 'files', 'operation_logs',
    'error_logs', 'notifications'
)
GROUP BY TABLE_NAME, INDEX_NAME, INDEX_TYPE, NON_UNIQUE
ORDER BY TABLE_NAME, INDEX_NAME;

SELECT 'All indexes created successfully! Performance optimized.' AS message;
