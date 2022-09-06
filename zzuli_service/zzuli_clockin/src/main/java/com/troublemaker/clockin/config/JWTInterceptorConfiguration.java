package com.troublemaker.clockin.config;

import com.troublemaker.clockin.interceptor.JWTAuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.config
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  21:06
 * @Version: 1.0
 */
@Configuration
public class JWTInterceptorConfiguration implements  WebMvcConfigurer {

    // 添加到拦截链中
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor())
                .addPathPatterns("/**");    // 拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
    }

    // 注册到spring容器中
    @Bean
    public JWTAuthenticationInterceptor jwtAuthenticationInterceptor() {
        return new JWTAuthenticationInterceptor();
    }
}

