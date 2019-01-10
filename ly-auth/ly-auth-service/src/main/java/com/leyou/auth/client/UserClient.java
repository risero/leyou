package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Controller;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/2 14:37
 */
@FeignClient("user-service")
@Controller
public interface UserClient extends UserApi {

}
