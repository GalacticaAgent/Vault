package com.vault.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

/**
 * Spring Security 工具类
 * <p>
 * 提供便捷方法从 Spring Security 上下文中获取当前登录用户信息
 * <p>
 * 使用场景：
 * - Controller 中获取当前登录用户ID
 * - Service 中获取当前用户名
 * - 需要当前用户角色进行权限判断
 * <p>
 * 使用示例：
 * <pre>
 * // 在 Controller 中获取当前学生ID
 * Long studentId = SecurityUtil.getCurrentUserId();
 *
 * // 获取当前用户名
 * String username = SecurityUtil.getCurrentUsername();
 *
 * // 获取当前用户角色
 * String role = SecurityUtil.getCurrentUserRole();
 * </pre>
 * <p>
 * 注意事项：
 * 1. 必须在已认证的请求中使用（带有有效的 JWT Token）
 * 2. 如果在未认证的上下文中调用，会抛出 IllegalStateException
 * 3. JwtAuthenticationFilter 负责将用户信息存入 Security 上下文
 *
 * @author Vault Team
 * @since 2026-01-21
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户的 ID
     * <p>
     * 从 Spring Security 上下文的 Authentication.details 中提取 userId
     * <p>
     * 使用场景：Controller 需要获取当前操作用户的ID来查询数据
     *
     * @return 当前登录用户的ID
     * @throws IllegalStateException 如果用户未认证或认证信息不完整
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getAuthentication();
        Map<String, Object> details = getDetails(authentication);

        Object userId = details.get("userId");
        if (userId == null) {
            throw new IllegalStateException("无法获取当前用户ID，认证信息不完整");
        }

        return (Long) userId;
    }

    /**
     * 获取当前登录用户的用户名
     * <p>
     * 从 Authentication.principal 中获取用户名（JwtAuthenticationFilter 存入的）
     *
     * @return 当前登录用户的用户名
     * @throws IllegalStateException 如果用户未认证
     */
    public static String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof String) {
            return (String) principal;
        }

        // 兜底方案：从 details 中获取
        Map<String, Object> details = getDetails(authentication);
        Object username = details.get("username");
        if (username == null) {
            throw new IllegalStateException("无法获取当前用户名，认证信息不完整");
        }

        return (String) username;
    }

    /**
     * 获取当前登录用户的角色
     * <p>
     * 角色值：STUDENT, TEACHER, ADMIN
     *
     * @return 当前登录用户的角色
     * @throws IllegalStateException 如果用户未认证或认证信息不完整
     */
    public static String getCurrentUserRole() {
        Authentication authentication = getAuthentication();
        Map<String, Object> details = getDetails(authentication);

        Object role = details.get("role");
        if (role == null) {
            throw new IllegalStateException("无法获取当前用户角色，认证信息不完整");
        }

        return (String) role;
    }

    /**
     * 判断当前用户是否是学生
     *
     * @return true 如果是学生角色
     */
    public static boolean isStudent() {
        return "STUDENT".equals(getCurrentUserRole());
    }

    /**
     * 判断当前用户是否是教师
     *
     * @return true 如果是教师角色
     */
    public static boolean isTeacher() {
        return "TEACHER".equals(getCurrentUserRole());
    }

    /**
     * 判断当前用户是否是管理员
     *
     * @return true 如果是管理员角色
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    /**
     * 获取当前用户的完整认证信息
     * <p>
     * 包含 userId, username, role 的 Map
     *
     * @return 用户详细信息Map
     * @throws IllegalStateException 如果用户未认证
     */
    public static Map<String, Object> getCurrentUserDetails() {
        Authentication authentication = getAuthentication();
        return getDetails(authentication);
    }

    /**
     * 获取当前认证对象
     *
     * @return Authentication 对象
     * @throws IllegalStateException 如果用户未认证
     */
    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("用户未认证，请先登录");
        }

        // 排除 anonymousUser（Spring Security 默认的匿名用户）
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("用户未认证，请先登录");
        }

        return authentication;
    }

    /**
     * 从 Authentication 中提取 details（Map类型）
     *
     * @param authentication 认证对象
     * @return details Map
     * @throws IllegalStateException 如果 details 不存在或类型错误
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDetails(Authentication authentication) {
        Object details = authentication.getDetails();

        if (details == null) {
            throw new IllegalStateException("认证信息不完整，缺少 details");
        }

        if (!(details instanceof Map)) {
            throw new IllegalStateException("认证信息格式错误，details 应为 Map 类型");
        }

        return (Map<String, Object>) details;
    }
}
