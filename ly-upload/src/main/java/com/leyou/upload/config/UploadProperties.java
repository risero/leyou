package com.leyou.upload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/3 20:23
 */
@ConfigurationProperties(prefix = "ly.upload")
@Setter
@Getter
public class UploadProperties {

    private String base_url; // 图片的域名
    private List<String> allowTypes; // 图片后缀名
}
