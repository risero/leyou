package com.leyou.registry.gateway.filter;

import com.leyo.auth.entity.UserInfo;
import com.leyo.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.registry.gateway.config.FilterProperties;
import com.leyou.registry.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 登录过滤器
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/2 18:35
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE; // 过滤器类型，前置过滤
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;// 过滤器顺序
    }

    // 是否过滤
    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();

        // 获取请求的url路径
        String path = request.getRequestURI();

        // 如果是配置的url路径则不进行过滤，否则过滤
        return !isAllowPath(path);
    }

    /**
     * 判断是否是配置的uri路径，false则进行过滤，true则不过滤
     *
     * @param path 请求的路径
     * @return
     */
    private boolean isAllowPath(String path) {
        for (String allowPath : filterProperties.getAllowPaths()) {
            // 判断是否允许
            if(path.startsWith(allowPath)){
                return true;
            }
        }
        return false;
    }

    // 过滤内容
    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 解析token
        try {
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName(), false);
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            // 校验权限
        } catch (Exception e) {
            // 解析token失败，未登录，拦截
            ctx.setSendZuulResponse(false);
            // 返回状态码
            ctx.setResponseStatusCode(403);
        }
        // 返回null，放行
        return null;
    }
}
