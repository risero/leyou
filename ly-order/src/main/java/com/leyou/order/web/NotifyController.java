package com.leyou.order.web;

import com.github.wxpay.sdk.WXPayConstants;
import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付回调Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/9 21:36
 */
@Slf4j
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Autowired
    private OrderService orderService;

    /**
     * 微信支付成功回调
     *
     * @param result
     * @return
     */
    @PostMapping(value = "/pay", produces = "application/xml")//指定返回的xml格式
    public Map hello(@RequestBody Map<String, String> result) {// RequestBody作用：把请求的json数据绑定到对象中
        // 处理回调
        orderService.handleNotify(result);

        log.info("[支付回调] 接收微信支付回调，结果：{}", result);

        // 返回成功
        Map msg = new HashMap<>();
        msg.put("return_code", WXPayConstants.SUCCESS);
        msg.put("return_msg", "OK");
        return msg;
    }
}
