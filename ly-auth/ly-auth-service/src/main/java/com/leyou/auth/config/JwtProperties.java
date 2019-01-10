package com.leyou.auth.config;

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

    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;
    private String cookieName;
    private int cookieMaxAge;

    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥

    // 对象一旦实例化后，就应该读取公钥和私钥，只生成一次
    @PostConstruct // @PostConstruct：在构造函数执行完毕以后执行
    public void init() throws Exception {
        // 公钥私钥如果不存在，先生成
        File pubPath = new File(pubKeyPath);
        File priPath = new File(priKeyPath);
        if(!pubPath.exists() || !priPath.exists()){
            RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
        }
        // 公钥和私钥存在，读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}
