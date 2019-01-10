package com.leyou.registry.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/11/27 21:02
 */
@SpringCloudApplication
@EnableZuulProxy // 开启Zuul
public class LyGateway {
    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class);
    }
}
