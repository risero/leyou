package com.leyou.order.web;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import com.leyou.order.utils.PayHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/7 9:52
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        Long orderId = orderService.dcreateOrder(orderDTO);
        return ResponseEntity.ok(orderId);
    }

    /**
     * 根据orderId查询订单
     *
     * @param orderId
     * @return
     */
    @GetMapping("{orderId}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("orderId") Long orderId){
        Order order = orderService.queryOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * 创建支付连接
     *
     * @param orderId
     * @return
     */
    @GetMapping("/url/{orderId}")
    public ResponseEntity<String> createPayUrl(@PathVariable("orderId") Long orderId){
        String url = orderService.createPayUrl(orderId);
        return ResponseEntity.ok(url);
    }

    /**
     * 根据orderId查询订单状态
     *
     * @param orderId
     * @return
     */
    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> queryOrderState(@PathVariable("id") Long orderId){
        // 获取支付状态
        Integer state = orderService.queryOrderState(orderId).getPayStateCode();
        return ResponseEntity.ok(state);
    }
}
