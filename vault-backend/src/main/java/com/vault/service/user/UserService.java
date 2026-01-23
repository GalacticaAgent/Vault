package com.vault.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.dto.request.UpdateUserRequest;
import com.vault.dto.response.UserResponse;
import com.vault.entity.mysql.User;
import com.vault.exception.BusinessException;
import com.vault.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

/**
 * 用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    @Value("${vault.file.upload-dir:uploads}")
    private String uploadDir;
    
    private String absoluteUploadPath;
    
    /**
     * 初始化上传目录（使用绝对路径）
     */
    @PostConstruct
    public void init() {
        try {
            // 如果是相对路径，转换为绝对路径
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.isAbsolute()) {
                // 使用项目根目录
                uploadDirFile = new File(System.getProperty("user.dir"), uploadDir);
            }
            absoluteUploadPath = uploadDirFile.getAbsolutePath();
            
            // 确保目录存在
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
                log.info("创建上传目录: {}", absoluteUploadPath);
            }
            
            log.info("文件上传目录初始化完成: {}", absoluteUploadPath);
        } catch (Exception e) {
            log.error("初始化上传目录失败", e);
            throw new RuntimeException("初始化上传目录失败", e);
        }
    }
    
    /**
     * 获取用户信息
     */
    public UserResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        return convertToResponse(user);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional
    public UserResponse updateUserInfo(Long userId, UpdateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 更新昵称
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        
        // 更新邮箱
        if (request.getEmail() != null) {
            // 检查邮箱是否已被使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, request.getEmail())
                   .ne(User::getId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        // 更新头像
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        
        userMapper.updateById(user);
        
        log.info("Updated user info for user {}: {}", userId, request);
        return convertToResponse(user);
    }
    
    /**
     * 上传并更新用户头像
     */
    @Transactional
    public UserResponse uploadAvatar(Long userId, MultipartFile file) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(400, "只能上传图片文件");
        }
        
        // 限制文件大小 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(400, "文件大小不能超过5MB");
        }
        
        try {
            // 保存文件
            String avatarUrl = saveAvatarFile(file, userId);
            
            // 更新数据库
            user.setAvatar(avatarUrl);
            userMapper.updateById(user);
            
            log.info("Updated avatar for user {}: {}", userId, avatarUrl);
            return convertToResponse(user);
            
        } catch (IOException e) {
            log.error("Failed to upload avatar for user {}", userId, e);
            throw new BusinessException(500, "头像上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存头像文件
     */
    private String saveAvatarFile(MultipartFile file, Long userId) throws IOException {
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 生成文件路径：uploads/avatars/yyyy/MM/dd/userId_uuid.ext
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = userId + "_" + UUID.randomUUID().toString() + extension;
        
        // 使用绝对路径创建目录
        Path dirPath = Paths.get(absoluteUploadPath, "avatars", datePath);
        Files.createDirectories(dirPath);
        
        Path filePath = dirPath.resolve(fileName);
        file.transferTo(filePath.toFile());
        
        log.info("文件已保存到: {}", filePath.toAbsolutePath());
        
        // 返回相对路径作为URL（用于前端访问）
        // 使用正斜杠确保跨平台兼容
        return "/uploads/avatars/" + datePath.replace("\\", "/") + "/" + fileName;
    }
    
    /**
     * 转换为响应对象
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createTime(user.getCreateTime())
                .lastLoginTime(user.getLastLoginTime())
                .build();
    }
}
