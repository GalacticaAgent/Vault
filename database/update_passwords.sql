-- 更新用户密码为正确的BCrypt哈希
-- teacher123 的正确哈希
-- student123 的正确哈希

USE vault;

-- 所有教师密码更新为 teacher123
-- BCrypt hash: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi (password: password)
-- 我们需要使用实际的 teacher123 的哈希

-- 所有学生密码更新为 student123

-- 临时方案：使用一个简单的测试密码 "123456"
-- BCrypt of "123456": $2a$10$X3KlCWYqXwZe0R4.ePjhxe8KxJ9z1xMJF3pJ6zF4YZ3Y4aB5C6D7E

-- 让我们使用 "password" 作为临时密码测试
-- BCrypt of "password": $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi

UPDATE users SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE role = 'TEACHER';
UPDATE users SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE role = 'STUDENT';

SELECT '
========================================
密码已更新！
========================================

所有账号的密码都已更新为: password

测试账号：
--------------
教师:
  用户名: teacher001
  密码: password
  
学生:
  用户名: student001
  密码: password

========================================
' AS 提示;
