package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 标准库存单元对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/5 16:52
 */
@Table(name = "tb_spu_detail")
@Data
public class SpuDetail {

    @Id
    private Long spuId; //对应的spu的id
    private String description; //商品描述
    private String genericSpec; //商品的全局规格属性
    private String specialSpec; //商品特殊规格的名称以及可选值模板
    private String packingList; //包装清单
    private String afterService; //售后服务
}
