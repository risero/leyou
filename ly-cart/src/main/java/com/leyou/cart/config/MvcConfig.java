package com.leyou.cart.config;

import com.leyou.cart.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Mvc配置类
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/3 17:41
 */
@Configuration // 配置类
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(userInterceptor());
        // 拦截购物车服务的一切资源
        interceptor.addPathPatterns("/**");
    }

    /**
     * 在拦截器我们使用了spring的注解，我们需要把拦截器交给spring管理，而不是我们自己new
     * 或者我们不在拦截器中使用Spring的注解，自己去new拦截器
     *
     * @return
     */
    @Bean
    public UserInterceptor userInterceptor(){
        return new UserInterceptor();
    }
}
