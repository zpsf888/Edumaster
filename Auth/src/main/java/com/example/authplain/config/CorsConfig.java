package com.example.authplain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有路径应用 CORS 配置
                .allowedOriginPatterns("*") // 允许所有来源，可以根据需要调整
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有头部
                .allowCredentials(true) // 允许发送凭证
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }
}
