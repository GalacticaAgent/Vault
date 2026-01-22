package com.vault.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek AI 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API URL
     */
    private String apiUrl;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 最大生成token数
     */
    private Integer maxTokens;

    /**
     * 温度参数（0-1，越高越随机）
     */
    private Double temperature;

    /**
     * 请求超时时间（毫秒）
     */
    private Long timeout;
}
