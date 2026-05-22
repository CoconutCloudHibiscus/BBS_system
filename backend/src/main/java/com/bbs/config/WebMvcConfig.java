package com.bbs.config;

import com.bbs.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * <p>注册JWT拦截器，配置拦截和排除路径</p>
 * <p>公开只读接口的GET请求在JwtInterceptor内部判断放行，写操作必须认证</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册拦截器
     * <p>JWT拦截器拦截所有路径，仅排除登录、注册和文档路径</p>
     * <p>公开GET请求（浏览帖子、板块等）在拦截器内部按HTTP方法判断放行</p>
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 认证相关接口（仅登录和注册）
                        "/api/auth/login",
                        "/api/auth/register",
                        // Swagger / OpenAPI 文档
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        // 静态资源
                        "/webjars/**",
                        "/v3/api-docs/**"
                );
    }
}
