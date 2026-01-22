-- =============================================
-- Vault 数据库验证脚本
-- 描述: 验证数据库安装是否正确
-- 版本: 1.0
-- 日期: 2026-01-21
-- =============================================

USE vault;

SELECT '
╔═══════════════════════════════════════════════════╗
║                                                   ║
║       Vault Database Validation Script           ║
║                                                   ║
╚═══════════════════════════════════════════════════╝
' AS '';

-- =============================================
-- 1. 检查数据库基本信息
-- =============================================
SELECT '
----------------------------------------------------
1. Database Information
----------------------------------------------------
' AS '';

SELECT 
    DATABASE() AS current_database,
    @@character_set_database AS charset,
    @@collation_database AS collation;

-- =============================================
-- 2. 检查表数量和状态
-- =============================================
SELECT '
----------------------------------------------------
2. Table Count and Status
----------------------------------------------------
' AS '';

SELECT 
    COUNT(*) AS total_tables,
    SUM(TABLE_ROWS) AS total_rows,
    ROUND(SUM(DATA_LENGTH) / 1024 / 1024, 2) AS data_size_mb,
    ROUND(SUM(INDEX_LENGTH) / 1024 / 1024, 2) AS index_size_mb
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'vault' 
AND TABLE_TYPE = 'BASE TABLE';

-- 列出所有表
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) AS size_mb,
    ENGINE,
    TABLE_COLLATION
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'vault' 
AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- =============================================
-- 3. 检查外键约束
-- =============================================
SELECT '
----------------------------------------------------
3. Foreign Key Constraints
----------------------------------------------------
' AS '';

SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'vault'
AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;

-- =============================================
-- 4. 检查索引
-- =============================================
SELECT '
----------------------------------------------------
4. Index Information
----------------------------------------------------
' AS '';

SELECT 
    TABLE_NAME,
    INDEX_NAME,
    INDEX_TYPE,
    NON_UNIQUE,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS columns
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'vault'
GROUP BY TABLE_NAME, INDEX_NAME, INDEX_TYPE, NON_UNIQUE
ORDER BY TABLE_NAME, INDEX_NAME;

-- =============================================
-- 5. 检查视图
-- =============================================
SELECT '
----------------------------------------------------
5. Views
----------------------------------------------------
' AS '';

SELECT 
    TABLE_NAME AS view_name,
    IS_UPDATABLE,
    CHECK_OPTION,
    DEFINER
FROM information_schema.VIEWS
WHERE TABLE_SCHEMA = 'vault'
ORDER BY TABLE_NAME;

-- =============================================
-- 6. 检查存储过程
-- =============================================
SELECT '
----------------------------------------------------
6. Stored Procedures
----------------------------------------------------
' AS '';

SELECT 
    ROUTINE_NAME AS procedure_name,
    ROUTINE_TYPE,
    DTD_IDENTIFIER AS return_type,
    SQL_DATA_ACCESS,
    CREATED,
    LAST_ALTERED
FROM information_schema.ROUTINES
WHERE ROUTINE_SCHEMA = 'vault'
AND ROUTINE_TYPE = 'PROCEDURE'
ORDER BY ROUTINE_NAME;

-- =============================================
-- 7. 检查函数
-- =============================================
SELECT '
----------------------------------------------------
7. Functions
----------------------------------------------------
' AS '';

SELECT 
    ROUTINE_NAME AS function_name,
    ROUTINE_TYPE,
    DTD_IDENTIFIER AS return_type,
    IS_DETERMINISTIC,
    SQL_DATA_ACCESS,
    CREATED
FROM information_schema.ROUTINES
WHERE ROUTINE_SCHEMA = 'vault'
AND ROUTINE_TYPE = 'FUNCTION'
ORDER BY ROUTINE_NAME;

-- =============================================
-- 8. 检查触发器
-- =============================================
SELECT '
----------------------------------------------------
8. Triggers
----------------------------------------------------
' AS '';

SELECT 
    TRIGGER_NAME,
    EVENT_MANIPULATION AS event,
    EVENT_OBJECT_TABLE AS table_name,
    ACTION_TIMING AS timing,
    ACTION_STATEMENT
FROM information_schema.TRIGGERS
WHERE TRIGGER_SCHEMA = 'vault'
ORDER BY EVENT_OBJECT_TABLE, TRIGGER_NAME;

-- =============================================
-- 9. 检查用户数据
-- =============================================
SELECT '
----------------------------------------------------
9. Initial Data Check
----------------------------------------------------
' AS '';

-- 用户统计
SELECT 
    role,
    COUNT(*) AS count
FROM users
GROUP BY role;

-- 学生统计
SELECT 
    COUNT(*) AS student_count,
    COUNT(DISTINCT grade) AS grade_count,
    COUNT(DISTINCT class_name) AS class_count
FROM students;

-- 教师统计
SELECT COUNT(*) AS teacher_count FROM teachers;

-- 问卷统计
SELECT 
    COUNT(*) AS questionnaire_count,
    COUNT(CASE WHEN status = 'PUBLISHED' THEN 1 END) AS published_count
FROM questionnaires;

-- 题目统计
SELECT COUNT(*) AS question_count FROM questions;

-- 资料统计
SELECT COUNT(*) AS material_count FROM materials;

-- Skills统计
SELECT COUNT(*) AS skills_count FROM skills;

-- =============================================
-- 10. 测试基本查询
-- =============================================
SELECT '
----------------------------------------------------
10. Test Basic Queries
----------------------------------------------------
' AS '';

-- 测试视图
SELECT '测试视图 v_user_full_info:' AS test;
SELECT COUNT(*) AS record_count FROM v_user_full_info;

SELECT '测试视图 v_student_ranking:' AS test;
SELECT COUNT(*) AS record_count FROM v_student_ranking;

SELECT '测试视图 v_questionnaire_stats:' AS test;
SELECT COUNT(*) AS record_count FROM v_questionnaire_stats;

-- =============================================
-- 11. 测试函数
-- =============================================
SELECT '
----------------------------------------------------
11. Test Functions
----------------------------------------------------
' AS '';

-- 测试随机字符串生成
SELECT fn_generate_random_string(10) AS random_string;

-- 测试分享ID生成
SELECT fn_generate_share_id() AS share_id;

-- 测试计算通过率（如果有问卷提交）
SELECT fn_calculate_pass_rate(1) AS pass_rate;

-- =============================================
-- 12. 检查数据完整性
-- =============================================
SELECT '
----------------------------------------------------
12. Data Integrity Check
----------------------------------------------------
' AS '';

-- 检查孤立的学生记录
SELECT 
    'Orphaned Students' AS check_type,
    COUNT(*) AS count
FROM students s
LEFT JOIN users u ON s.user_id = u.id
WHERE u.id IS NULL;

-- 检查孤立的教师记录
SELECT 
    'Orphaned Teachers' AS check_type,
    COUNT(*) AS count
FROM teachers t
LEFT JOIN users u ON t.user_id = u.id
WHERE u.id IS NULL;

-- 检查孤立的消息记录
SELECT 
    'Orphaned Messages' AS check_type,
    COUNT(*) AS count
FROM messages m
LEFT JOIN chats c ON m.chat_id = c.id
WHERE c.id IS NULL;

-- 检查孤立的答案记录
SELECT 
    'Orphaned Answers' AS check_type,
    COUNT(*) AS count
FROM answers a
LEFT JOIN questions q ON a.question_id = q.id
WHERE q.id IS NULL;

-- =============================================
-- 13. 性能检查
-- =============================================
SELECT '
----------------------------------------------------
13. Performance Check
----------------------------------------------------
' AS '';

-- 检查未使用的索引（需要运行一段时间后才有意义）
SELECT 
    s.TABLE_NAME,
    s.INDEX_NAME,
    s.COLUMN_NAME,
    'Potentially Unused' AS status
FROM information_schema.STATISTICS s
WHERE s.TABLE_SCHEMA = 'vault'
AND s.INDEX_NAME NOT IN ('PRIMARY')
AND NOT EXISTS (
    SELECT 1 
    FROM information_schema.KEY_COLUMN_USAGE k
    WHERE k.TABLE_SCHEMA = 'vault'
    AND k.TABLE_NAME = s.TABLE_NAME
    AND k.CONSTRAINT_NAME = s.INDEX_NAME
    AND k.REFERENCED_TABLE_NAME IS NOT NULL
)
ORDER BY s.TABLE_NAME, s.INDEX_NAME;

-- =============================================
-- 14. 总结
-- =============================================
SELECT '
----------------------------------------------------
Validation Summary
----------------------------------------------------
' AS '';

SELECT 
    'Tables' AS item,
    COUNT(*) AS count
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'vault' AND TABLE_TYPE = 'BASE TABLE'

UNION ALL

SELECT 
    'Views' AS item,
    COUNT(*) AS count
FROM information_schema.VIEWS 
WHERE TABLE_SCHEMA = 'vault'

UNION ALL

SELECT 
    'Stored Procedures' AS item,
    COUNT(*) AS count
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = 'vault' AND ROUTINE_TYPE = 'PROCEDURE'

UNION ALL

SELECT 
    'Functions' AS item,
    COUNT(*) AS count
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = 'vault' AND ROUTINE_TYPE = 'FUNCTION'

UNION ALL

SELECT 
    'Triggers' AS item,
    COUNT(*) AS count
FROM information_schema.TRIGGERS 
WHERE TRIGGER_SCHEMA = 'vault'

UNION ALL

SELECT 
    'Users' AS item,
    COUNT(*) AS count
FROM users;

SELECT '
╔═══════════════════════════════════════════════════╗
║                                                   ║
║         Validation Completed Successfully         ║
║                                                   ║
║  Please review the results above                  ║
║  - All counts should match expected values        ║
║  - No orphaned records should exist               ║
║  - All foreign keys should be valid               ║
║                                                   ║
╚═══════════════════════════════════════════════════╝
' AS '';
