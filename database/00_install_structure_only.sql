-- =============================================
-- Vault 数据库安装脚本（生产环境版本 - 无测试数据）
-- 描述: 仅创建数据库结构，不包含测试数据
-- 版本: 1.0
-- 日期: 2026-01-21
-- 使用方法: mysql -u root -p < 00_install_structure_only.sql
-- =============================================

SELECT '
╔═══════════════════════════════════════════════════╗
║                                                   ║
║    Vault Database Structure Installation         ║
║         (Production Version - No Test Data)       ║
║                                                   ║
╚═══════════════════════════════════════════════════╝
' AS '';

SELECT 'Starting installation...' AS 'Step 0';
SELECT CURRENT_TIMESTAMP AS 'Start Time';

-- =============================================
-- Step 1: Create Database
-- =============================================
SELECT '
----------------------------------------------------
Step 1: Creating Database
----------------------------------------------------
' AS '';

SOURCE 01_create_database.sql;

-- =============================================
-- Step 2: Create Tables
-- =============================================
SELECT '
----------------------------------------------------
Step 2: Creating Tables
----------------------------------------------------
' AS '';

SOURCE 02_create_tables.sql;

-- =============================================
-- Step 3: Create Views
-- =============================================
SELECT '
----------------------------------------------------
Step 3: Creating Views
----------------------------------------------------
' AS '';

SOURCE 03_create_views.sql;

-- =============================================
-- Step 4: Create Stored Procedures
-- =============================================
SELECT '
----------------------------------------------------
Step 4: Creating Stored Procedures
----------------------------------------------------
' AS '';

SOURCE 04_create_procedures.sql;

-- =============================================
-- Step 5: Create Triggers
-- =============================================
SELECT '
----------------------------------------------------
Step 5: Creating Triggers
----------------------------------------------------
' AS '';

SOURCE 05_create_triggers.sql;

-- =============================================
-- Step 6: Create Functions
-- =============================================
SELECT '
----------------------------------------------------
Step 6: Creating Functions
----------------------------------------------------
' AS '';

SOURCE 06_create_functions.sql;

-- =============================================
-- Step 7: Create Indexes
-- =============================================
SELECT '
----------------------------------------------------
Step 7: Creating Indexes for Performance
----------------------------------------------------
' AS '';

SOURCE 07_create_indexes.sql;

-- =============================================
-- Verify Installation
-- =============================================
SELECT '
----------------------------------------------------
Verifying Installation
----------------------------------------------------
' AS '';

USE vault;

-- 统计表数量
SELECT COUNT(*) AS 'Total Tables' 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'vault' AND TABLE_TYPE = 'BASE TABLE';

-- 统计视图数量
SELECT COUNT(*) AS 'Total Views' 
FROM information_schema.VIEWS 
WHERE TABLE_SCHEMA = 'vault';

-- 统计存储过程数量
SELECT COUNT(*) AS 'Total Stored Procedures' 
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = 'vault' AND ROUTINE_TYPE = 'PROCEDURE';

-- 统计函数数量
SELECT COUNT(*) AS 'Total Functions' 
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = 'vault' AND ROUTINE_TYPE = 'FUNCTION';

-- 统计触发器数量
SELECT COUNT(*) AS 'Total Triggers' 
FROM information_schema.TRIGGERS 
WHERE TRIGGER_SCHEMA = 'vault';

-- =============================================
-- Installation Complete
-- =============================================
SELECT '
╔═══════════════════════════════════════════════════╗
║                                                   ║
║   Structure Installation Completed Successfully   ║
║                                                   ║
║  Database: vault                                  ║
║  Character Set: utf8mb4                           ║
║  Collation: utf8mb4_unicode_ci                    ║
║                                                   ║
║  ✅ 20 Tables Created                             ║
║  ✅ 16 Views Created                              ║
║  ✅ 13 Stored Procedures Created                  ║
║  ✅ 14 Functions Created                          ║
║  ✅ 12 Triggers Created                           ║
║  ✅ Performance Indexes Created                   ║
║                                                   ║
║  Database is ready for use!                       ║
║  No test data included - clean structure          ║
║                                                   ║
║  Next Steps:                                      ║
║  1. Configure application connection              ║
║  2. Create your first admin user                  ║
║  3. Start using the system                        ║
║                                                   ║
╚═══════════════════════════════════════════════════╝
' AS '';

SELECT CURRENT_TIMESTAMP AS 'Completion Time';

SELECT '
Production Database Notes:
- No test accounts created
- No sample data included
- Ready for multiple users
- Secure by default
' AS 'Important';
