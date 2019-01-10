package com.leyou.registry.gateway.config;

import com.leyo.auth.utils.RsaUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/1 18:48
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;
    private String cookieName;

    private PublicKey publicKey; // 公钥

    @PostConstruct // @PostConstruct：在构造函数执行完毕以后执行
    public void init() throws Exception {
        // 读取公钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
