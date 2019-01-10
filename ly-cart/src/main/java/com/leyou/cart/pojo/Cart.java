package com.leyou.cart.pojo;

import lombok.Data;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2019/1/3 19:17
 */
@Data
public class Cart {

    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
}
