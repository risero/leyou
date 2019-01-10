package com.leyou.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页对象
 * vo：view Object，视图对象，专门给视图用的
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/1 13:08
 */
@Data
public class PageResult<T> {

    private Long total; //总条数
    private Integer totalPage; //总页数
    private List<T> items; //当前页数据

    public PageResult(){}

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
