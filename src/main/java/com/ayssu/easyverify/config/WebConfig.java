package com.ayssu.easyverify.config;

import com.ayssu.easyverify.interceptor.AuthInterceptor;
import com.ayssu.easyverify.interceptor.LocalhostInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final LocalhostInterceptor localhostInterceptor;

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 本地访问拦截器：限制 /system/** 只能通过 localhost 或 127.0.0.1 访问
        registry.addInterceptor(localhostInterceptor)
                .addPathPatterns("/system/**");
        
        // 认证拦截器：拦截所有管理端接口
        registry.addInterceptor(authInterceptor)
                // 拦截所有管理端接口
                .addPathPatterns("/api/admin/**")
                // 排除登录、超级管理员注册、系统状态查询、Token验证接口
                .excludePathPatterns(
                        "/api/admin/auth/login",
                        "/api/admin/auth/send-register-code",
                        "/api/admin/auth/register-super-admin",
                        "/api/admin/auth/system-status",
                        "/api/admin/auth/validate-token",
                        "/other/callback",
                        "/wechat/heartbeat"
                );
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")  // 允许所有域名（生产环境请指定具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 允许携带 Cookie
                .maxAge(3600);
    }
}
