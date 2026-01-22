package com.vault.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. 从请求头中提取 Token
            String token = extractTokenFromRequest(request);

            // 2. 如果 Token 存在且有效，则设置认证信息
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                // 3. 从 Token 中解析用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // 4. 创建权限列表（Spring Security 要求角色需要 ROLE_ 前缀）
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(role),
                        new SimpleGrantedAuthority("ROLE_" + role)
                );
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("[PROTECTED]")
                        .authorities(authorities)
                        .build();

                // 5. 创建 Authentication 对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,  // principal（主体）- 存储用户详情
                                null,      // credentials（凭证）- 密码，已验证后不需要
                                authorities  // authorities（权限）
                        );

                // 6. ⭐ 关键步骤：将 userId 等信息存入 Authentication 的 details 中
                // 这样 SecurityUtil 就可以通过 details 获取 userId
                Map<String, Object> details = new HashMap<>();
                details.put("userId", userId);
                details.put("username", username);
                details.put("role", role);
                authentication.setDetails(details);

                // 7. 将 Authentication 存入 Spring Security 上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("✅ JWT认证成功: userId={}, username={}, role={}, authorities={}",
                        userId, username, role, authentication.getAuthorities());
            }

        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
            // 认证失败不抛出异常，继续执行过滤链
            // Spring Security 会检测到没有认证信息，返回 401
            // Token验证失败，清除认证信息
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 Token
     * <p>
     * 请求头格式: Authorization: Bearer <token>
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 去掉 "Bearer " 前缀，返回实际的 Token
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * 判断是否跳过该请求的过滤
     * <p>
     * 对于公开接口（如登录、注册），不需要进行 JWT 验证
     * 这些路径已在 SecurityConfig 中配置为 permitAll()
     *
     * 注意：由于 application.yml 配置了 context-path: /api，
     * 所以 servletPath 中不包含 /api 前缀，只包含相对路径
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // 公开路径，不需要 JWT 验证
        // 注意：不需要添加 /api 前缀（context-path已处理）
        return path.startsWith("/auth/") ||
               path.startsWith("/health/") ||
               path.startsWith("/doc.html") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/favicon.ico");
    }
}
