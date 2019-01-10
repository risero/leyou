package com.leyou.auth;

import com.leyo.auth.entity.UserInfo;
import com.leyo.auth.utils.JwtUtils;
import com.leyo.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/1 13:44
 */
public class JwtTest {

    // 公钥和秘钥生成的位置
    private static final String pubKeyPath = "D:\\rsa.pub";
    private static final String priKeyPath = "D:\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception{
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    /**
     * 获取公钥和秘钥设置到对象中
     *
     * @throws Exception
     */
    @Test
    @Before
    public void testGetRsa() throws Exception{
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     *
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception{
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"),
                privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU0NjMyMzEyOX0.PTVFlscnF9nlzwTLdx1TBNSV5RdZff44nBrgID0wWJgc2u9v0TzcK3XCJC6xcE2cL47zaKGGN2G1sRXM1pRNfpYxLE7uj4BeAas-nDg1oXzf92vPPpox6BvjKgOQle6YOpQV-8Lg_oRgg1wR59if7e7CnZQdZjA67QAI2T2V-tk";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
