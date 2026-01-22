package com.vault.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.dto.request.CreateShareRequest;
import com.vault.dto.response.ChatResponse;
import com.vault.dto.response.ShareResponse;
import com.vault.entity.mysql.Chat;
import com.vault.entity.mysql.ChatShare;
import com.vault.mapper.ChatMapper;
import com.vault.mapper.ChatShareMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天分享服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatShareService {
    
    private final ChatShareMapper chatShareMapper;
    private final ChatMapper chatMapper;
    private final ChatService chatService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 创建分享
     */
    @Transactional
    public ShareResponse createShare(Long userId, CreateShareRequest request) {
        // 验证会话所有权
        Chat chat = chatMapper.selectById(request.getChatId());
        if (chat == null || !chat.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在");
        }
        
        // 创建分享记录
        ChatShare share = new ChatShare();
        share.setShareId(UUID.randomUUID().toString().replace("-", ""));
        share.setChatId(request.getChatId());
        share.setUserId(userId);
        share.setTitle(request.getTitle() != null ? request.getTitle() : chat.getTitle());
        share.setDescription(request.getDescription());
        share.setViewCount(0);
        share.setStatus("active");
        share.setRequirePassword(request.getRequirePassword());
        
        // 设置过期时间
        if (request.getExpireDays() != null) {
            share.setExpireTime(LocalDateTime.now().plusDays(request.getExpireDays()));
        }
        
        // 加密密码
        if (request.getRequirePassword() && request.getPassword() != null) {
            share.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        chatShareMapper.insert(share);
        
        log.info("Created share {} for chat {} by user {}", 
                share.getShareId(), request.getChatId(), userId);
        
        // 构建响应
        return ShareResponse.builder()
                .shareId(share.getShareId())
                .shareUrl("/share/" + share.getShareId())
                .title(share.getTitle())
                .description(share.getDescription())
                .viewCount(0)
                .expireTime(share.getExpireTime())
                .requirePassword(share.getRequirePassword())
                .createTime(share.getCreateTime())
                .build();
    }
    
    /**
     * 获取分享内容
     */
    public ChatResponse getShare(String shareId, String password) {
        // 查询分享记录
        ChatShare share = chatShareMapper.getByShareId(shareId);
        if (share == null) {
            throw new RuntimeException("分享不存在");
        }
        
        // 检查状态
        if (!"active".equals(share.getStatus())) {
            throw new RuntimeException("分享已失效");
        }
        
        // 检查过期时间
        if (share.getExpireTime() != null && LocalDateTime.now().isAfter(share.getExpireTime())) {
            throw new RuntimeException("分享已过期");
        }
        
        // 验证密码
        if (share.getRequirePassword()) {
            if (password == null || !passwordEncoder.matches(password, share.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        }
        
        // 增加访问次数
        chatShareMapper.incrementViewCount(share.getId());
        
        // 获取会话内容
        Chat chat = chatMapper.selectById(share.getChatId());
        ChatResponse response = chatService.getChatDetail(chat.getUserId(), chat.getId());
        
        log.info("Share {} accessed, total views: {}", shareId, share.getViewCount() + 1);
        
        return response;
    }
    
    /**
     * 撤销分享
     */
    public void revokeShare(Long userId, String shareId) {
        ChatShare share = chatShareMapper.getByShareId(shareId);
        if (share == null || !share.getUserId().equals(userId)) {
            throw new RuntimeException("分享不存在");
        }
        
        chatShareMapper.revokeShare(share.getId());
        
        log.info("Revoked share {} by user {}", shareId, userId);
    }
}
