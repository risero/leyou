package com.leyou.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.leyou.order.interceptor.*;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/6 20:17
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(userInterceptor());
        interceptor.addPathPatterns("/order/**");
    }

    @Bean
    public UserInterceptor userInterceptor(){
        return new UserInterceptor();
    }
}
