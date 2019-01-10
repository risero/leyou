package com.leyou.page.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Brand远程调用api
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/13 17:00
 */
@FeignClient(value = "item-service", path = "/brand")
public interface BrandClient extends BrandApi {

}
