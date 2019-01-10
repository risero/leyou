package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/4 21:06
 */
@Data
@Table(name = "tb_spec_param")
public class SpecParam {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;

    @Column(name="`numeric`") // 因为numeric在MySQL中是关键字，所以使用``转义成普通字符串
    private Boolean numeric;
    private String unit;
    private Boolean generic; // 是否是sku通用属性
    private Boolean searching; // 是否是可搜索过滤的
    private String segments; // 是否是数值类型参数
}
