package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/19 15:46
 */
@SpringBootApplication
@EnableDiscoveryClient // 启动Eureka发现探索
@EnableFeignClients // 启动feign
public class LyPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class);
    }
}
