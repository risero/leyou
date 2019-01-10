package com.leyou.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/11/27 20:52
 */
@SpringBootApplication
@EnableEurekaServer //启动Eureka注册中心
public class LyRegistry {
    public static void main(String[] args) {
        SpringApplication.run(LyRegistry.class, args);
    }
}
