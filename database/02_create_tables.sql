-- =============================================
-- Vault 数据表创建脚本
-- 描述: 创建所有核心业务表
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

-- =============================================
-- 1. 用户相关表
-- =============================================

-- 1.1 用户表
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL COMMENT '角色',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1.2 学生表
CREATE TABLE `students` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `student_number` VARCHAR(20) NOT NULL COMMENT '学号',
    `major` VARCHAR(100) COMMENT '专业',
    `grade` VARCHAR(20) COMMENT '年级',
    `class_name` VARCHAR(50) COMMENT '班级',
    `total_questions` INT DEFAULT 0 COMMENT '提问总数',
    `total_scores` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '总分数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_number` (`student_number`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_grade_class` (`grade`, `class_name`),
    CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 1.3 教师表
CREATE TABLE `teachers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教师ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `teacher_number` VARCHAR(20) NOT NULL COMMENT '工号',
    `department` VARCHAR(100) COMMENT '所属院系',
    `title` VARCHAR(50) COMMENT '职称',
    `courses` JSON COMMENT '授课课程列表',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_number` (`teacher_number`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_department` (`department`),
    CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- =============================================
-- 2. 聊天相关表
-- =============================================

-- 2.1 对话表
CREATE TABLE `chats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '对话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '对话标题',
    `shared` BOOLEAN DEFAULT FALSE COMMENT '是否分享',
    `share_id` VARCHAR(50) COMMENT '分享ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_update_time` (`update_time`),
    UNIQUE KEY `uk_share_id` (`share_id`),
    CONSTRAINT `fk_chat_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话表';

-- 2.2 消息表
CREATE TABLE `messages` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `chat_id` BIGINT NOT NULL COMMENT '对话ID',
    `role` ENUM('user', 'assistant', 'system') NOT NULL COMMENT '角色',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `file_ids` JSON COMMENT '附件文件ID列表',
    `references` JSON COMMENT '参考资料列表',
    `skills` JSON COMMENT '使用的Skills列表',
    `tokens` INT COMMENT '消耗的token数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_chat_id` (`chat_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_chat_create` (`chat_id`, `create_time`),
    CONSTRAINT `fk_message_chat` FOREIGN KEY (`chat_id`) REFERENCES `chats` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- =============================================
-- 3. 问卷相关表
-- =============================================

-- 3.1 问卷表
CREATE TABLE `questionnaires` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问卷ID',
    `title` VARCHAR(200) NOT NULL COMMENT '问卷标题',
    `description` TEXT COMMENT '问卷描述',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID（教师）',
    `target_students` JSON COMMENT '目标学生ID列表（空表示全班）',
    `time_limit` INT NOT NULL COMMENT '时间限制（分钟）',
    `total_score` DECIMAL(10, 2) NOT NULL COMMENT '总分',
    `pass_score` DECIMAL(10, 2) COMMENT '及格分数',
    `start_time` DATETIME COMMENT '开始时间',
    `deadline` DATETIME COMMENT '截止时间',
    `status` ENUM('DRAFT', 'PUBLISHED', 'CLOSED') DEFAULT 'DRAFT' COMMENT '状态',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deadline` (`deadline`),
    CONSTRAINT `fk_questionnaire_creator` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷表';

-- 3.2 题目表
CREATE TABLE `questions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `questionnaire_id` BIGINT NOT NULL COMMENT '问卷ID',
    `question_order` INT NOT NULL COMMENT '题目顺序',
    `type` ENUM('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER', 'CODING') NOT NULL COMMENT '题目类型',
    `content` TEXT NOT NULL COMMENT '题目内容',
    `options` JSON COMMENT '选项（选择题使用）',
    `correct_answer` TEXT COMMENT '正确答案',
    `knowledge_point` VARCHAR(100) COMMENT '关联知识点',
    `difficulty` ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM' COMMENT '难度',
    `score` DECIMAL(10, 2) NOT NULL COMMENT '分值',
    `explanation` TEXT COMMENT '题目解析',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_questionnaire_id` (`questionnaire_id`),
    KEY `idx_knowledge_point` (`knowledge_point`),
    KEY `idx_difficulty` (`difficulty`),
    CONSTRAINT `fk_question_questionnaire` FOREIGN KEY (`questionnaire_id`) REFERENCES `questionnaires` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 3.3 答案表
CREATE TABLE `answers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '答案ID',
    `questionnaire_id` BIGINT NOT NULL COMMENT '问卷ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `answer_content` TEXT COMMENT '答案内容',
    `is_correct` BOOLEAN COMMENT '是否正确',
    `score` DECIMAL(10, 2) COMMENT '得分',
    `feedback` TEXT COMMENT 'AI反馈',
    `time_spent` INT COMMENT '答题用时（秒）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_questionnaire_student` (`questionnaire_id`, `student_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_student_question` (`student_id`, `question_id`),
    CONSTRAINT `fk_answer_questionnaire` FOREIGN KEY (`questionnaire_id`) REFERENCES `questionnaires` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_answer_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答案表';

-- 3.4 问卷提交记录表
CREATE TABLE `questionnaire_submissions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
    `questionnaire_id` BIGINT NOT NULL COMMENT '问卷ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `total_score` DECIMAL(10, 2) NOT NULL COMMENT '总得分',
    `time_spent` INT NOT NULL COMMENT '总用时（秒）',
    `submit_time` DATETIME NOT NULL COMMENT '提交时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_questionnaire_student` (`questionnaire_id`, `student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_submit_time` (`submit_time`),
    KEY `idx_student_submit` (`student_id`, `submit_time`),
    CONSTRAINT `fk_submission_questionnaire` FOREIGN KEY (`questionnaire_id`) REFERENCES `questionnaires` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_submission_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷提交记录表';

-- =============================================
-- 4. 资料相关表
-- =============================================

-- 4.1 资料表
CREATE TABLE `materials` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资料ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
    `description` TEXT COMMENT '资料描述',
    `type` ENUM('TEXTBOOK', 'LECTURE', 'VIDEO', 'EXAM', 'OTHER') NOT NULL COMMENT '资料类型',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `file_type` VARCHAR(50) COMMENT '文件类型（MIME）',
    `content` LONGTEXT COMMENT '提取的文本内容',
    `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
    `is_public` BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    `target_classes` JSON COMMENT '目标班级列表',
    `knowledge_points` JSON COMMENT '关联知识点列表',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`),
    FULLTEXT KEY `ft_title_description` (`title`, `description`),
    CONSTRAINT `fk_material_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料表';

-- =============================================
-- 5. 代码仓库相关表
-- =============================================

-- 5.1 代码仓库表
CREATE TABLE `code_repositories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `lab_name` VARCHAR(100) NOT NULL COMMENT '实验名称',
    `repo_url` VARCHAR(500) NOT NULL COMMENT '仓库URL',
    `branch` VARCHAR(100) DEFAULT 'main' COMMENT '分支名',
    `last_commit_hash` VARCHAR(100) COMMENT '最后一次提交哈希',
    `last_analysis_time` DATETIME COMMENT '最后分析时间',
    `analysis_result` JSON COMMENT '分析结果',
    `ai_detection_score` DECIMAL(5, 2) COMMENT 'AI检测分数（0-100）',
    `code_quality_score` DECIMAL(5, 2) COMMENT '代码质量分数（0-100）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_lab_name` (`lab_name`),
    CONSTRAINT `fk_repo_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码仓库表';

-- 5.2 代码提交记录表
CREATE TABLE `code_commits` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
    `repository_id` BIGINT NOT NULL COMMENT '仓库ID',
    `commit_hash` VARCHAR(100) NOT NULL COMMENT '提交哈希',
    `commit_message` TEXT COMMENT '提交信息',
    `author` VARCHAR(100) COMMENT '提交者',
    `commit_time` DATETIME NOT NULL COMMENT '提交时间',
    `files_changed` INT COMMENT '修改文件数',
    `lines_added` INT COMMENT '新增行数',
    `lines_deleted` INT COMMENT '删除行数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_repository_id` (`repository_id`),
    KEY `idx_commit_time` (`commit_time`),
    CONSTRAINT `fk_commit_repository` FOREIGN KEY (`repository_id`) REFERENCES `code_repositories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码提交记录表';

-- =============================================
-- 6. Skills 相关表
-- =============================================

-- 6.1 Skills 表
CREATE TABLE `skills` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Skill ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'Skill名称',
    `description` TEXT COMMENT 'Skill描述',
    `category` ENUM('CHAT', 'QUESTIONNAIRE', 'CODE_ANALYSIS', 'OTHER') NOT NULL COMMENT 'Skill分类',
    `content` LONGTEXT NOT NULL COMMENT 'Skill内容（Markdown）',
    `parameters` JSON COMMENT '参数定义',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `usage_count` INT DEFAULT 0 COMMENT '使用次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_enabled` (`enabled`),
    CONSTRAINT `fk_skill_creator` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skills表';

-- =============================================
-- 7. 看板相关表
-- =============================================

-- 7.1 自定义卡片表
CREATE TABLE `dashboard_cards` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '卡片ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '卡片标题',
    `query` TEXT NOT NULL COMMENT '查询描述',
    `content` TEXT COMMENT '卡片内容',
    `refresh_interval` INT DEFAULT 0 COMMENT '刷新间隔（毫秒，0表示不自动刷新）',
    `card_order` INT DEFAULT 0 COMMENT '卡片顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_card_order` (`card_order`),
    CONSTRAINT `fk_card_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义卡片表';

-- =============================================
-- 8. 文件表
-- =============================================

-- 8.1 文件表
CREATE TABLE `files` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `stored_name` VARCHAR(255) NOT NULL COMMENT '存储文件名',
    `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型',
    `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
    `storage_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
    `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_file_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- =============================================
-- 9. 系统日志表
-- =============================================

-- 9.1 操作日志表
CREATE TABLE `operation_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT COMMENT '用户ID',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `method` VARCHAR(10) COMMENT '请求方法',
    `params` JSON COMMENT '请求参数',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `location` VARCHAR(100) COMMENT '操作地点',
    `status` TINYINT DEFAULT 1 COMMENT '操作状态（1成功 0失败）',
    `error_msg` TEXT COMMENT '错误信息',
    `execution_time` INT COMMENT '执行时间（毫秒）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 9.2 错误日志表
CREATE TABLE `error_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT COMMENT '用户ID',
    `error_type` VARCHAR(50) NOT NULL COMMENT '错误类型',
    `error_message` TEXT NOT NULL COMMENT '错误信息',
    `stack_trace` LONGTEXT COMMENT '堆栈跟踪',
    `request_url` VARCHAR(500) COMMENT '请求URL',
    `request_params` JSON COMMENT '请求参数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_error_type` (`error_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错误日志表';

-- =============================================
-- 10. 通知相关表
-- =============================================

-- 10.1 通知表
CREATE TABLE `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `type` ENUM('SYSTEM', 'QUESTIONNAIRE', 'MATERIAL', 'CHAT', 'OTHER') NOT NULL COMMENT '通知类型',
    `related_id` BIGINT COMMENT '关联ID',
    `is_read` BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

SELECT 'All tables created successfully!' AS message;
