-- 修复 chats 表缺失的字段
USE vault;

-- 添加 status 字段
SET @check_status = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'status');
SET @sql_status = IF(@check_status = 0, 
    'ALTER TABLE `chats` ADD COLUMN `status` VARCHAR(20) DEFAULT ''active'' COMMENT ''状态'' AFTER `title`', 
    'SELECT "status already exists"');
PREPARE stmt FROM @sql_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 last_message 字段
SET @check_last_message = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'last_message');
SET @sql_last_message = IF(@check_last_message = 0, 
    'ALTER TABLE `chats` ADD COLUMN `last_message` TEXT COMMENT ''最后一条消息'' AFTER `status`', 
    'SELECT "last_message already exists"');
PREPARE stmt FROM @sql_last_message;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 last_message_time 字段
SET @check_last_message_time = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'last_message_time');
SET @sql_last_message_time = IF(@check_last_message_time = 0, 
    'ALTER TABLE `chats` ADD COLUMN `last_message_time` DATETIME COMMENT ''最后消息时间'' AFTER `last_message`', 
    'SELECT "last_message_time already exists"');
PREPARE stmt FROM @sql_last_message_time;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 message_count 字段
SET @check_message_count = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'message_count');
SET @sql_message_count = IF(@check_message_count = 0, 
    'ALTER TABLE `chats` ADD COLUMN `message_count` INT DEFAULT 0 COMMENT ''消息总数'' AFTER `last_message_time`', 
    'SELECT "message_count already exists"');
PREPARE stmt FROM @sql_message_count;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 pinned 字段
SET @check_pinned = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'pinned');
SET @sql_pinned = IF(@check_pinned = 0, 
    'ALTER TABLE `chats` ADD COLUMN `pinned` BOOLEAN DEFAULT FALSE COMMENT ''是否置顶'' AFTER `message_count`', 
    'SELECT "pinned already exists"');
PREPARE stmt FROM @sql_pinned;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 shared 字段
SET @check_shared = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'shared');
SET @sql_shared = IF(@check_shared = 0, 
    'ALTER TABLE `chats` ADD COLUMN `shared` BOOLEAN DEFAULT FALSE COMMENT ''是否分享'' AFTER `pinned`', 
    'SELECT "shared already exists"');
PREPARE stmt FROM @sql_shared;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 share_id 字段
SET @check_share_id = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'share_id');
SET @sql_share_id = IF(@check_share_id = 0, 
    'ALTER TABLE `chats` ADD COLUMN `share_id` VARCHAR(50) COMMENT ''分享ID'' AFTER `shared`', 
    'SELECT "share_id already exists"');
PREPARE stmt FROM @sql_share_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 deleted 字段
SET @check_deleted = (SELECT COUNT(*) FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'vault' AND TABLE_NAME = 'chats' AND COLUMN_NAME = 'deleted');
SET @sql_deleted = IF(@check_deleted = 0, 
    'ALTER TABLE `chats` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT ''逻辑删除标记'' AFTER `share_id`', 
    'SELECT "deleted already exists"');
PREPARE stmt FROM @sql_deleted;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 查看最终表结构
DESCRIBE chats;

SELECT '修复完成！chats 表已更新' AS 提示;
