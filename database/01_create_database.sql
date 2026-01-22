-- =============================================
-- Vault 数据库创建脚本
-- 描述: 创建 Vault 数据库及基本配置
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

-- 删除已存在的数据库（谨慎使用）
DROP DATABASE IF EXISTS vault;

-- 创建数据库
CREATE DATABASE vault
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE vault;

-- 设置时区
SET time_zone = '+08:00';

-- 设置字符集
SET NAMES utf8mb4;

-- 创建数据库版本记录表
CREATE TABLE `db_version` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '版本ID',
    `version` VARCHAR(20) NOT NULL COMMENT '版本号',
    `description` TEXT COMMENT '版本描述',
    `applied_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '应用时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库版本管理表';

-- 插入初始版本
INSERT INTO `db_version` (`version`, `description`) VALUES ('1.0.0', '初始数据库结构');

-- 打印创建成功信息
SELECT 'Database vault created successfully!' AS message;
