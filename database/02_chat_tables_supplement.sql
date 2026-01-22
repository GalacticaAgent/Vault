-- =============================================
-- Vault 聊天系统补充表
-- 描述: 为智能问答系统添加缺失的表和字段
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

-- =============================================
-- 修改现有的 chats 表，添加缺失的字段
-- =============================================

-- 添加字段前先检查是否存在，避免重复添加
SET @check_message_count = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'message_count');
SET @sql_message_count = IF(@check_message_count = 0, 
    'ALTER TABLE `chats` ADD COLUMN `message_count` INT DEFAULT 0 COMMENT ''消息总数'' AFTER `last_message_time`', 
    'SELECT "message_count already exists"');
PREPARE stmt FROM @sql_message_count;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @check_pinned = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'pinned');
SET @sql_pinned = IF(@check_pinned = 0, 
    'ALTER TABLE `chats` ADD COLUMN `pinned` BOOLEAN DEFAULT FALSE COMMENT ''是否置顶'' AFTER `message_count`', 
    'SELECT "pinned already exists"');
PREPARE stmt FROM @sql_pinned;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @check_deleted = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'deleted');
SET @sql_deleted = IF(@check_deleted = 0, 
    'ALTER TABLE `chats` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT ''逻辑删除标记'' AFTER `pinned`', 
    'SELECT "deleted already exists"');
PREPARE stmt FROM @sql_deleted;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================
-- 修改现有的 messages 表，添加缺失的字段
-- =============================================

-- 添加 error_message 字段
SET @check_error = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'messages' AND COLUMN_NAME = 'error_message');
SET @sql_error = IF(@check_error = 0, 
    'ALTER TABLE `messages` ADD COLUMN `error_message` TEXT COMMENT ''错误信息（如果失败）'' AFTER `status`', 
    'SELECT "error_message already exists"');
PREPARE stmt FROM @sql_error;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 deleted 字段
SET @check_msg_deleted = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'messages' AND COLUMN_NAME = 'deleted');
SET @sql_msg_deleted = IF(@check_msg_deleted = 0, 
    'ALTER TABLE `messages` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT ''逻辑删除标记'' AFTER `error_message`', 
    'SELECT "deleted already exists"');
PREPARE stmt FROM @sql_msg_deleted;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================
-- 创建 chat_files 表（文件上传）
-- =============================================

CREATE TABLE IF NOT EXISTS `chat_files` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `chat_id` BIGINT COMMENT '所属会话ID',
    `message_id` BIGINT COMMENT '所属消息ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `file_name` VARCHAR(200) NOT NULL COMMENT '文件原始名称',
    `file_type` VARCHAR(20) COMMENT '文件类型：code-代码文件，pdf-PDF文档，image-图片，other-其他',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `file_path` VARCHAR(500) COMMENT '文件存储路径（MinIO）',
    `file_url` VARCHAR(500) COMMENT '文件URL',
    `extracted_content` LONGTEXT COMMENT '提取的文本内容',
    `language` VARCHAR(50) COMMENT '编程语言（如果是代码文件）',
    `status` VARCHAR(20) DEFAULT 'completed' COMMENT '处理状态：pending-待处理，processing-处理中，completed-已完成，failed-失败',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_chat_id` (`chat_id`),
    KEY `idx_message_id` (`message_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_file_type` (`file_type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_chat_file_chat` FOREIGN KEY (`chat_id`) REFERENCES `chats` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_file_message` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_file_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天文件表';

-- =============================================
-- 创建 chat_shares 表（对话分享）
-- =============================================

CREATE TABLE IF NOT EXISTS `chat_shares` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分享ID',
    `share_id` VARCHAR(50) NOT NULL COMMENT '分享的唯一标识符（UUID）',
    `chat_id` BIGINT NOT NULL COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '分享者用户ID',
    `title` VARCHAR(200) COMMENT '分享标题',
    `description` TEXT COMMENT '分享描述',
    `view_count` INT DEFAULT 0 COMMENT '访问次数',
    `expire_time` DATETIME COMMENT '过期时间（null表示永不过期）',
    `require_password` BOOLEAN DEFAULT FALSE COMMENT '是否需要密码',
    `password` VARCHAR(100) COMMENT '访问密码（加密存储）',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-活跃，expired-已过期，revoked-已撤销',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` INT DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_share_id` (`share_id`),
    KEY `idx_chat_id` (`chat_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_chat_share_chat` FOREIGN KEY (`chat_id`) REFERENCES `chats` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_chat_share_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话分享表';

-- =============================================
-- 更新 chats 表，移除旧的 share_id 列（已移到 chat_shares 表）
-- =============================================

-- 检查并删除 shared 列（如果存在）
SET @check_shared = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'shared');
SET @sql_drop_shared = IF(@check_shared > 0, 'ALTER TABLE `chats` DROP COLUMN `shared`', 'SELECT "Column shared does not exist"');
PREPARE stmt FROM @sql_drop_shared;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并删除 share_id 列（如果存在）
SET @check_share_id = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'share_id');
SET @sql_drop_share_id = IF(@check_share_id > 0, 'ALTER TABLE `chats` DROP COLUMN `share_id`', 'SELECT "Column share_id does not exist"');
PREPARE stmt FROM @sql_drop_share_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================
-- 创建索引优化
-- =============================================

-- 为聊天表添加复合索引（如果不存在）
SET @check_idx1 = (SELECT COUNT(*) FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND INDEX_NAME = 'idx_user_status');
SET @sql_idx1 = IF(@check_idx1 = 0, 
    'ALTER TABLE `chats` ADD KEY `idx_user_status` (`user_id`, `status`)', 
    'SELECT "idx_user_status already exists"');
PREPARE stmt FROM @sql_idx1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @check_idx2 = (SELECT COUNT(*) FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND INDEX_NAME = 'idx_user_time');
SET @sql_idx2 = IF(@check_idx2 = 0, 
    'ALTER TABLE `chats` ADD KEY `idx_user_time` (`user_id`, `last_message_time`)', 
    'SELECT "idx_user_time already exists"');
PREPARE stmt FROM @sql_idx2;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为消息表添加复合索引（如果不存在）
SET @check_idx3 = (SELECT COUNT(*) FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'messages' AND INDEX_NAME = 'idx_chat_role');
SET @sql_idx3 = IF(@check_idx3 = 0, 
    'ALTER TABLE `messages` ADD KEY `idx_chat_role` (`chat_id`, `role`)', 
    'SELECT "idx_chat_role already exists"');
PREPARE stmt FROM @sql_idx3;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @check_idx4 = (SELECT COUNT(*) FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'messages' AND INDEX_NAME = 'idx_chat_time');
SET @sql_idx4 = IF(@check_idx4 = 0, 
    'ALTER TABLE `messages` ADD KEY `idx_chat_time` (`chat_id`, `create_time`)', 
    'SELECT "idx_chat_time already exists"');
PREPARE stmt FROM @sql_idx4;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================
-- 验证表结构
-- =============================================

-- 显示表结构
SHOW CREATE TABLE `chats`;
SHOW CREATE TABLE `messages`;
SHOW CREATE TABLE `chat_files`;
SHOW CREATE TABLE `chat_shares`;

-- 显示表信息
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    AVG_ROW_LENGTH,
    DATA_LENGTH,
    INDEX_LENGTH,
    CREATE_TIME,
    UPDATE_TIME,
    TABLE_COMMENT
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'vault' 
    AND TABLE_NAME IN ('chats', 'messages', 'chat_files', 'chat_shares')
ORDER BY TABLE_NAME;

-- =============================================
-- 完成提示
-- =============================================

SELECT '智能问答系统数据库表创建完成！' AS Status;
SELECT '包含的表：chats, messages, chat_files, chat_shares' AS Tables;
SELECT '可以开始使用智能问答功能了！' AS Message;
