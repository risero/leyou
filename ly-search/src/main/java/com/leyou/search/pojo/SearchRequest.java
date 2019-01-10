package com.leyou.search.pojo;

import lombok.Data;

import java.util.Map;

/**
 * 搜索查询对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/15 14:35
 */
public class SearchRequest {

    private String key; // 搜索条件

    private Integer page; //当前页

    private Map<String, String> filter; // 过滤项

    private static final int DEFAULT_PAGE = 1; // 默认页

    /**
     * 每页大小 20条数据
     * 显示的页面不能通过用户传，如果用户乱传个非常大的实现数据，索引库会支撑不了。
     */
    private static final int DEFULT_SIZE = 20;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        /*
         *  获取页码时做一些校验，不能小于1
         *      如果page参数是负数，则返回DEFAULT_PAGE
         *      如果page参数是正数，则返回page
         */
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFULT_SIZE;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }
}
