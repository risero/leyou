package com.leyou.registry.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域拦截器
 * 解决域名跨域问题
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/11/29 19:03
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        // 1、添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 1)、允许的域，不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://manager.leyou.com");
        config.addAllowedOrigin("http://www.leyou.com");

        // 2)、是否发送Cookie信息
        config.setAllowCredentials(true);

        // 3)、允许的请求方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");

        // 4)、允许的头信息
        config.addAllowedHeader("*");

        // 5)、有效时长
        config.setMaxAge(3600L);

        // 2、添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config); //对所有资源跨域都进行拦截

        // 3、返回新的CorsFilter
        return new CorsFilter(configSource);
    }
}
