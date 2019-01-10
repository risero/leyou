package com.leyou.order.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 订单状态枚举对象
 */
@AllArgsConstructor
@NoArgsConstructor
public enum OrderStatusEnum {

    UN_PAY(1, "未付款"),
    PAYED(2, "已付款"),
    DELIVERED(3, "已发货，未确认"),
    SUCCESS(4, "已确认，未评价"),
    CLOSED(5, "交易失败，已关闭"),
    RATED(6, "已评价"),
    ;

    private int code;
    private String desc;

    /**
     * 返回当前枚举对象的状态码
     *
     * @return
     */
    public int getStatusCode(){
        return this.code;
    }
}
