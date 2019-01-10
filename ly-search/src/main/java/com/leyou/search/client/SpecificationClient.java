package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service", path = "/spec")
public interface SpecificationClient extends SpecificationApi {
}
