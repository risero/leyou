package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/11/29 16:55
 */
@Data
@Table(name="tb_category")
public class Category {

    @Id
    @KeySql(useGeneratedKeys = true) // 主键自增
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;
}
