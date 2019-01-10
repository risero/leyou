package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 标准商品库存对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/7 19:40
 */
@Table(name = "tb_sku")
@Data
public class Sku {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long spuId; // 商品单元id
    private String title; // 标题
    private String images; // 图片
    private Long price; // 价格
    private String ownSpec; // 特有参数
    private String indexes; // 特有规格属性在spu属性模板中的对应索引组合
    private Boolean enable; //是否可用
    private Date createTime; //创建时间
    private Date lastUpdateTime; //修改时间

    @Transient // 对该字段忽略映射
    private Integer stock; //库存
}
