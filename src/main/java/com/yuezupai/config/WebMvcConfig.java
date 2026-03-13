package com.yuezupai.config;

import com.yuezupai.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${file.local.path}")
    private String uploadPath;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /** 注册鉴权拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/v1/**")       // 拦截所有 /v1/ 开头的接口
                .excludePathPatterns(            // 放行不需要登录的接口
                        "/v1/auth/**",           // 登录相关
                        "/v1/item/list",         // 物品列表（公开）
                        "/v1/item/detail/**",    // 物品详情（公开）
                        "/v1/demand/list",       // 求租列表（公开）
                        "/v1/review/list"         // 评价列表（公开）
                );
    }

    /** 本地上传文件映射为静态资源（开发环境用，生产用Nginx代理） */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
    }
}