package com.leyou.page.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/12 20:13
 */
@FeignClient(value = "item-service", path = "/category")
public interface CategoryClient extends CategoryApi {
}
