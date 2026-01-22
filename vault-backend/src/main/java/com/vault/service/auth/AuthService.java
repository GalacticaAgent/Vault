package com.vault.service.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.dto.request.LoginRequest;
import com.vault.dto.request.RegisterRequest;
import com.vault.dto.response.LoginResponse;
import com.vault.entity.mysql.Student;
import com.vault.entity.mysql.Teacher;
import com.vault.entity.mysql.User;
import com.vault.exception.BusinessException;
import com.vault.mapper.StudentMapper;
import com.vault.mapper.TeacherMapper;
import com.vault.mapper.UserMapper;
import com.vault.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final JwtUtil jwtUtil;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 检查用户是否启用
        if (user.getEnabled() == null || !user.getEnabled()) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建用户信息
        LoginResponse.UserInfo userInfo = buildUserInfo(user);
        
        return LoginResponse.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }
    
    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole(request.getRole());
        user.setEnabled(true);
        user.setLastLoginTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 根据角色创建学生或教师信息
        if ("STUDENT".equals(request.getRole())) {
            createStudentProfile(user.getId(), request);
        } else if ("TEACHER".equals(request.getRole())) {
            createTeacherProfile(user.getId(), request);
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建用户信息
        LoginResponse.UserInfo userInfo = buildUserInfo(user);
        
        return LoginResponse.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }
    
    /**
     * 创建学生档案
     */
    private void createStudentProfile(Long userId, RegisterRequest request) {
        if (request.getStudentNumber() == null || request.getStudentNumber().isBlank()) {
            throw new BusinessException(400, "学生学号不能为空");
        }
        
        // 检查学号是否已存在
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNumber, request.getStudentNumber());
        if (studentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "学号已存在");
        }
        
        Student student = new Student();
        student.setUserId(userId);
        student.setStudentNumber(request.getStudentNumber());
        student.setMajor(request.getMajor());
        student.setGrade(request.getGrade());
        student.setClassName(request.getClassName());
        student.setTotalQuestions(0);
        student.setTotalScores(java.math.BigDecimal.ZERO);
        
        studentMapper.insert(student);
    }
    
    /**
     * 创建教师档案
     */
    private void createTeacherProfile(Long userId, RegisterRequest request) {
        if (request.getTeacherNumber() == null || request.getTeacherNumber().isBlank()) {
            throw new BusinessException(400, "教师工号不能为空");
        }
        
        // 检查工号是否已存在
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNumber, request.getTeacherNumber());
        if (teacherMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "工号已存在");
        }
        
        Teacher teacher = new Teacher();
        teacher.setUserId(userId);
        teacher.setTeacherNumber(request.getTeacherNumber());
        teacher.setDepartment(request.getDepartment());
        teacher.setTitle(request.getTitle());
        
        teacherMapper.insert(teacher);
    }
    
    /**
     * 构建用户信息
     */
    private LoginResponse.UserInfo buildUserInfo(User user) {
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
        
        // 如果是学生，查询学生信息
        if ("STUDENT".equals(user.getRole())) {
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Student::getUserId, user.getId());
            Student student = studentMapper.selectOne(wrapper);
            if (student != null) {
                userInfo.setStudentNumber(student.getStudentNumber());
                userInfo.setMajor(student.getMajor());
                userInfo.setGrade(student.getGrade());
                userInfo.setClassName(student.getClassName());
            }
        }
        
        // 如果是教师，查询教师信息
        if ("TEACHER".equals(user.getRole())) {
            LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teacher::getUserId, user.getId());
            Teacher teacher = teacherMapper.selectOne(wrapper);
            if (teacher != null) {
                userInfo.setTeacherNumber(teacher.getTeacherNumber());
                userInfo.setDepartment(teacher.getDepartment());
                userInfo.setTitle(teacher.getTitle());
            }
        }
        
        return userInfo;
    }
}

