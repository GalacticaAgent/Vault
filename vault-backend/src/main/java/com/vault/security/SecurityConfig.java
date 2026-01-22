package com.vault.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（使用JWT不需要CSRF保护）
            .csrf(AbstractHttpConfigurer::disable)
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置会话管理（无状态）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置认证失败处理
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问的端点
                .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                // 健康检查接口
                .requestMatchers("/health/**", "/api/health/**").permitAll()
                // Knife4j 和 Swagger 相关路径
                .requestMatchers("/doc.html", "/api/doc.html").permitAll()
                .requestMatchers("/swagger-ui.html", "/api/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**", "/api/swagger-ui/**").permitAll()
                .requestMatchers("/webjars/**", "/api/webjars/**").permitAll()
                .requestMatchers("/swagger-resources/**", "/api/swagger-resources/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/api/v3/api-docs/**").permitAll()
                .requestMatchers("/favicon.ico", "/api/favicon.ico").permitAll()
                // 教师端接口需要教师权限
                .requestMatchers("/api/teacher/**", "/teacher/**").hasRole("TEACHER")
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 添加JWT认证过滤器，在UsernamePasswordAuthenticationFilter之前执行
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
