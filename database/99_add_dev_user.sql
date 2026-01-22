-- 开发快捷用户 (User: 1, Pass: 1)
-- Password hash for '1': $2b$04$OIp0x0qnPykH/v.ekf8Vke1pYBMAErRDRX5.2N9iHKHeLr0qIqwbK
INSERT INTO users (username, password, email, nickname, role, enabled) VALUES
('1', '$2b$04$OIp0x0qnPykH/v.ekf8Vke1pYBMAErRDRX5.2N9iHKHeLr0qIqwbK', '1@dev.com', 'DevUser', 'STUDENT', TRUE);

-- 插入对应的学生记录
INSERT INTO students (user_id, student_number, major, grade, class_name) 
SELECT id, 'DEV001', 'DevMajor', '2024', 'DevClass' FROM users WHERE username = '1';
