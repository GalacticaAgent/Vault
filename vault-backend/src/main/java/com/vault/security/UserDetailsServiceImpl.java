package com.vault.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vault.entity.mysql.User;
import com.vault.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security 用户详情服务实现
 * <p>
 * 实现 UserDetailsService 接口，用于从数据库加载用户信息
 * <p>
 * 功能说明：
 * 1. Spring Security 在认证过程中会调用此服务加载用户信息
 * 2. 从 MySQL 数据库的 users 表中查询用户
 * 3. 将数据库中的 User 实体转换为 Spring Security 的 UserDetails
 * 4. 提供用户的用户名、密码、权限等信息供 Spring Security 进行认证和授权
 * <p>
 * 注意事项：
 * 1. 本项目使用 JWT 认证，通常不会直接调用此服务
 * 2. 如果未来需要支持用户名密码登录（非JWT），此服务会被使用
 * 3. 密码已使用 BCrypt 加密存储，Spring Security 会自动验证
 *
 * @author Vault Team
 * @since 2026-01-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    /**
     * 根据用户名加载用户信息
     * <p>
     * Spring Security 会调用此方法来获取用户详情，用于认证和授权
     *
     * @param username 用户名
     * @return UserDetails 包含用户信息的对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: username={}", username);

        // 1. 从数据库查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        // 2. 用户不存在，抛出异常
        if (user == null) {
            log.warn("用户不存在: username={}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 3. 用户已禁用，抛出异常
        if (user.getEnabled() == null || !user.getEnabled()) {
            log.warn("用户已禁用: username={}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 4. 构建权限列表（Spring Security 要求角色需要 ROLE_ 前缀）
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        // 5. 返回 Spring Security 的 UserDetails 对象
        // 使用 Spring Security 提供的 User 实现类
        log.debug("用户信息加载成功: username={}, role={}", username, user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())  // BCrypt 加密后的密码
                .authorities(Collections.singletonList(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getEnabled() == null || !user.getEnabled())
                .build();
    }
}
