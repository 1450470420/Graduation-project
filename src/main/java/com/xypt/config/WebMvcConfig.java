package com.xypt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置跨域（CORS）、静态资源访问（上传文件）等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 文件上传根路径（从配置文件读取） */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 配置跨域访问
     * 允许前端（Vue3开发服务器:5173）访问后端接口（:8080）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的前端域名，生产环境需限制为具体域名
                .allowedOriginPatterns("*")
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带Cookie（Session认证需要）
                .allowCredentials(true)
                // 预检请求缓存时间（秒）
                .maxAge(3600);
    }

    /**
     * 配置静态资源访问
     * 使上传的图片/文件可以通过 /upload/** URL访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /upload/ URL路径映射到本地文件系统的上传目录
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
