-- 测试数据
USE vault;

-- 插入测试用户（密码都是 password123）
-- BCrypt加密后的 password123: $2a$10$N9qo8uLOickgx2ZMRZoMye1J3qrJqDqPxKLjJqVxPqXqXqXqXqXqX
-- 注意：实际使用时需要用真实的BCrypt加密密码

-- 测试学生用户
INSERT INTO users (username, password, email, nickname, role, enabled) VALUES
('student1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J3qrJqDqPxKLjJqVxPqXqXqXqXqXqX', 'student1@example.com', '学生一', 'STUDENT', TRUE),
('student2', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J3qrJqDqPxKLjJqVxPqXqXqXqXqXqX', 'student2@example.com', '学生二', 'STUDENT', TRUE);

-- 测试教师用户
INSERT INTO users (username, password, email, nickname, role, enabled) VALUES
('teacher1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J3qrJqDqPxKLjJqVxPqXqXqXqXqXqX', 'teacher1@example.com', '教师一', 'TEACHER', TRUE),
('teacher2', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J3qrJqDqPxKLjJqVxPqXqXqXqXqXqX', 'teacher2@example.com', '教师二', 'TEACHER', TRUE);

-- 开发快捷用户 (User: 1, Pass: 1)
-- Password hash for '1': $2b$04$OIp0x0qnPykH/v.ekf8Vke1pYBMAErRDRX5.2N9iHKHeLr0qIqwbK
INSERT INTO users (username, password, email, nickname, role, enabled) VALUES
('1', '$2b$04$OIp0x0qnPykH/v.ekf8Vke1pYBMAErRDRX5.2N9iHKHeLr0qIqwbK', '1@dev.com', 'DevUser', 'STUDENT', TRUE);

-- 插入学生档案
-- 注意：这里使用子查询获取ID，以适应不同的自增状态
INSERT INTO students (user_id, student_number, major, grade, class_name) VALUES
((SELECT id FROM users WHERE username = 'student1'), '2024001', '计算机科学与技术', '2024级', '1班'),
((SELECT id FROM users WHERE username = 'student2'), '2024002', '软件工程', '2024级', '2班'),
((SELECT id FROM users WHERE username = '1'), 'DEV001', 'DevMajor', '2024', 'DevClass');

-- 插入教师档案
INSERT INTO teachers (user_id, teacher_number, department, title) VALUES
((SELECT id FROM users WHERE username = 'teacher1'), 'T001', '计算机学院', '教授'),
((SELECT id FROM users WHERE username = 'teacher2'), 'T002', '软件学院', '副教授');
