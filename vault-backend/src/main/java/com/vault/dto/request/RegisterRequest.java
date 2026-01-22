package com.vault.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字和下划线，长度4-20位")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "密码至少8位，包含字母和数字")
    private String password;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 角色：STUDENT, TEACHER
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(STUDENT|TEACHER)$", message = "角色只能是STUDENT或TEACHER")
    private String role;
    
    /**
     * 学号（学生必填）
     */
    private String studentNumber;
    
    /**
     * 专业（学生选填）
     */
    private String major;
    
    /**
     * 年级（学生选填）
     */
    private String grade;
    
    /**
     * 班级（学生选填）
     */
    private String className;
    
    /**
     * 工号（教师必填）
     */
    private String teacherNumber;
    
    /**
     * 院系（教师选填）
     */
    private String department;
    
    /**
     * 职称（教师选填）
     */
    private String title;
}

