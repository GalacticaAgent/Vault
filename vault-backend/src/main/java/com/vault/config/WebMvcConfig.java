package com.vault.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC配置
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${vault.file.upload-dir:uploads}")
    private String uploadDir;
    
    private String absoluteUploadPath;
    
    @PostConstruct
    public void init() {
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.isAbsolute()) {
            uploadDirFile = new File(System.getProperty("user.dir"), uploadDir);
        }
        absoluteUploadPath = uploadDirFile.getAbsolutePath();
        log.info("静态资源路径: {}", absoluteUploadPath);
    }
    
    /**
     * 配置静态资源映射
     * 将 /uploads/** 映射到实际的文件存储目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件路径，需要确保路径以分隔符结尾
        String uploadPath = absoluteUploadPath;
        if (!uploadPath.endsWith("/") && !uploadPath.endsWith("\\")) {
            uploadPath = uploadPath + File.separator;
        }
        
        // 转换为file协议URL
        String resourceLocation = "file:///" + uploadPath.replace("\\", "/");
        
        log.info("配置静态资源映射: /uploads/** -> {}", resourceLocation);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
