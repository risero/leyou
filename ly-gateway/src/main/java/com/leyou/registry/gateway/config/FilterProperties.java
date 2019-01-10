package com.leyou.registry.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/2 19:12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.filter")
public class FilterProperties {

    private List<String> allowPaths;
}
