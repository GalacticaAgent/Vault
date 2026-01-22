package com.vault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Vault/Radiant 后端应用主启动类
 *
 * @author Vault Team
 */
@SpringBootApplication
@EnableAsync
@MapperScan("com.vault.mapper")
public class VaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaultApplication.class, args);
    }
}

