package com.vault.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置会话管理（无状态）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置认证失败处理
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问的认证端点（登录、注册）
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
                // 学生端接口需要学生角色
                .requestMatchers("/student/**", "/api/student/**").hasRole("STUDENT")
                // 教师端接口需要教师角色
                .requestMatchers("/teacher/**", "/api/teacher/**").hasRole("TEACHER")
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
