package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/7 19:54
 */
@Table(name = "tb_stock")
@Data
public class Stock {

    @Id
    private Long skuId;
    private Integer seckillStock; // 秒杀可用库存
    private Integer seckillTotal; // 已秒杀总数
    private Integer stock; // 库存
}
