package com.leyou.page.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/12 20:52
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
