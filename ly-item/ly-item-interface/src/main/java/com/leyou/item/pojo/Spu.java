package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 标准商品单元（spu）对象
 * 一个spu对象对应多个商品库存单元（sku）对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/5 16:44
 */
@Table(name="tb_spu")
@Data
public class Spu {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId; //商品品牌id
    private Long cid1; //商品分类1级类目
    private Long cid2; //商品分类2级类目
    private Long cid3; //商品分类3级目录
    private String title; //标题
    private String subTitle; //子标题
    private Boolean saleable; //是否上架
    private Date createTime; //创建时间

    @JsonIgnore //返回json数据时，不返回该字段
    private Boolean valid; //是否有效，逻辑删除用

    @JsonIgnore //返回json数据时，不返回该字段
    private Date lastUpdateTime; //修改时间

    @Transient // 表示该属性并非是数据库表的字段，ORM框架将忽略该属性
    private String cname; //分类名称

    @Transient // 表示该属性并非是数据库表的字段，ORM框架将忽略该属性.
    private String bname; //品牌名称

    @Transient
    private List<Sku> skus; // 一对多，一个商品单元对应多个商品库存对象

    @Transient
    private SpuDetail spuDetail; // 商品详情对象
}
