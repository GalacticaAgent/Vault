package com.vault.controller.user;

import com.vault.common.Result;
import com.vault.dto.request.UpdateUserRequest;
import com.vault.dto.response.UserResponse;
import com.vault.service.user.UserService;
import com.vault.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理相关接口")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserResponse> getCurrentUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserResponse userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前用户的昵称、邮箱等信息")
    public Result<UserResponse> updateUserInfo(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserResponse userInfo = userService.updateUserInfo(userId, request);
        return Result.success(userInfo);
    }
    
    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像", description = "上传并更新当前用户的头像")
    public Result<UserResponse> uploadAvatar(
            @Parameter(description = "头像图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtil.getCurrentUserId();
        UserResponse userInfo = userService.uploadAvatar(userId, file);
        return Result.success(userInfo);
    }
}
