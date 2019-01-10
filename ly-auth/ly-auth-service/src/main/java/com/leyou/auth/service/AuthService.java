package com.leyou.auth.service;

import com.leyo.auth.entity.UserInfo;
import com.leyo.auth.utils.JwtUtils;
import com.leyo.auth.utils.RsaUtils;
import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/1 20:31
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        try{
            // 校验用户名密码
            User user = userClient.queryUserByUsernameAndPassword(username, password);
            if(user == null){
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            // 生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username)
                    , jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        }catch (Exception e) {
            log.error("[授权中心] 用户名或密码有误，用户名称：{}", username, e);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }
}
