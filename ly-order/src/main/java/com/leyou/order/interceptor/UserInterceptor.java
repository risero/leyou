package com.leyou.order.interceptor;

import com.leyo.auth.entity.UserInfo;
import com.leyo.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户拦截器
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/3 17:11
 */
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    /**
     * 在购物车请求之前进行拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 根据cookieName获取token信息
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        // 已登录
        try {
            // 解析token
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            // 传递user
            tl.set(userInfo);

            //  放行
            return true;
        } catch (Exception e) {
            // 未登录
            log.error("[购物车服务] 解析用户身份失败", e);
            return false;
        }
    }

    /**
     * 在视图渲染后，删除数据
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 最后用完数据，一定要清空
        tl.remove();
    }

    public static UserInfo getUser(){
        UserInfo userInfo = tl.get();
        return userInfo;
    }
}
