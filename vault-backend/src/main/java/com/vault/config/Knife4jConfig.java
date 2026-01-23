package com.vault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vault API 文档")
                        .version("1.0.0")
                        .description("Vault/Radiant 智能教学系统 API 接口文档")
                        .contact(new Contact()
                                .name("Vault Team")
                                .email("vault@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }

    /**
     * 认证模块API
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1. 认证模块")
                .pathsToMatch("/auth/**")
                .build();
    }

    /**
     * 学生模块API
     */
    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
                .group("2. 学生模块")
                .pathsToMatch("/student/**")
                .build();
    }

    /**
     * 教师模块API
     */
    @Bean
    public GroupedOpenApi teacherApi() {
        return GroupedOpenApi.builder()
                .group("3. 教师模块")
                .pathsToMatch("/teacher/**")
                .build();
    }

    /**
     * 聊天模块API
     */
    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
                .group("4. 聊天模块")
                .pathsToMatch("/chat/**")
                .build();
    }

    /**
     * 材料查询API（公共接口）
     */
    @Bean
    public GroupedOpenApi materialApi() {
        return GroupedOpenApi.builder()
                .group("5. 材料查询")
                .pathsToMatch("/material/**")
                .build();
    }

    /**
     * 技能模块API
     */
    @Bean
    public GroupedOpenApi skillApi() {
        return GroupedOpenApi.builder()
                .group("6. 技能模块")
                .pathsToMatch("/skill/**")
                .build();
    }

    /**
     * 所有API
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("0. 全部接口")
                .pathsToMatch("/**")
                .build();
    }
}
