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

-- 插入学生档案
INSERT INTO students (user_id, student_number, major, grade, class_name) VALUES
(1, '2024001', '计算机科学与技术', '2024级', '1班'),
(2, '2024002', '软件工程', '2024级', '2班');

-- 插入教师档案
INSERT INTO teachers (user_id, teacher_number, department, title) VALUES
(3, 'T001', '计算机学院', '教授'),
(4, 'T002', '软件学院', '副教授');

