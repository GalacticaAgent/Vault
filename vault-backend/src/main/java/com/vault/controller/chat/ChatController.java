package com.vault.controller.chat;

import com.vault.common.Result;
import com.vault.dto.request.CreateShareRequest;
import com.vault.dto.request.SendMessageRequest;
import com.vault.dto.response.ChatResponse;
import com.vault.dto.response.FileUploadResponse;
import com.vault.dto.response.MessageResponse;
import com.vault.dto.response.ShareResponse;
import com.vault.security.JwtUtil;
import com.vault.service.chat.ChatService;
import com.vault.service.chat.ChatShareService;
import com.vault.service.chat.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 聊天控制器
 * 提供智能问答相关的REST API
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "聊天系统", description = "智能问答API")
public class ChatController {
    
    private final ChatService chatService;
    private final FileService fileService;
    private final ChatShareService chatShareService;
    private final JwtUtil jwtUtil;
    
    /**
     * 发送消息（同步）
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送消息并获取AI回复（非流式）")
    public Result<MessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        MessageResponse response = chatService.sendMessage(userId, request);
        return Result.success(response);
    }
    
    /**
     * 获取用户的所有会话
     */
    @GetMapping("/list")
    @Operation(summary = "获取会话列表", description = "获取当前用户的所有聊天会话")
    public Result<List<ChatResponse>> listChats(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<ChatResponse> chats = chatService.listChats(userId);
        return Result.success(chats);
    }
    
    /**
     * 获取会话详情
     */
    @GetMapping("/{chatId}")
    @Operation(summary = "获取会话详情", description = "获取指定会话的所有消息")
    public Result<ChatResponse> getChatDetail(
            @Parameter(description = "会话ID") @PathVariable Long chatId,
            HttpServletRequest request) {
        
        Long userId = getUserIdFromRequest(request);
        ChatResponse chat = chatService.getChatDetail(userId, chatId);
        return Result.success(chat);
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/{chatId}")
    @Operation(summary = "删除会话", description = "删除指定的聊天会话")
    public Result<Void> deleteChat(
            @Parameter(description = "会话ID") @PathVariable Long chatId,
            HttpServletRequest request) {
        
        Long userId = getUserIdFromRequest(request);
        chatService.deleteChat(userId, chatId);
        return Result.success();
    }
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传代码文件或PDF文档")
    public Result<FileUploadResponse> uploadFile(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "会话ID") @RequestParam(required = false) Long chatId,
            @Parameter(description = "文件类型") @RequestParam(required = false) String fileType,
            HttpServletRequest request) {
        
        Long userId = getUserIdFromRequest(request);
        FileUploadResponse response = fileService.uploadFile(userId, chatId, file, fileType);
        return Result.success(response);
    }
    
    /**
     * 创建分享
     */
    @PostMapping("/share")
    @Operation(summary = "创建分享", description = "创建会话分享链接")
    public Result<ShareResponse> createShare(
            @Valid @RequestBody CreateShareRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        ShareResponse response = chatShareService.createShare(userId, request);
        return Result.success(response);
    }
    
    /**
     * 获取分享内容
     */
    @GetMapping("/share/{shareId}")
    @Operation(summary = "获取分享", description = "通过分享ID查看会话内容")
    public Result<ChatResponse> getShare(
            @Parameter(description = "分享ID") @PathVariable String shareId,
            @Parameter(description = "访问密码") @RequestParam(required = false) String password) {
        
        ChatResponse response = chatShareService.getShare(shareId, password);
        return Result.success(response);
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未授权");
    }
}
