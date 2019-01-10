package com.leyou.cart.config;

import com.leyo.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/3 17:08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;
    private String cookieName;
    private int cookieMaxAge;

    private PublicKey publicKey; // 公钥

    @PostConstruct // 对象一旦实例化后，就应该读取公钥
    public void init() throws Exception{
        this.publicKey =  RsaUtils.getPublicKey(pubKeyPath);
    }
}
