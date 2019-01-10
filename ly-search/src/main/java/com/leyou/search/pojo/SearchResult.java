package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 搜索分页对象
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/16 19:47
 */
@Data
public class SearchResult extends PageResult<Goods> {

    private List<Category> categorys; // 分类待选项

    private List<Brand> brands; // 品牌列表待选项

    private List<Map<String, Object>> specs; // 规格参数 key及待选项

    public SearchResult(){}

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categorys, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categorys = categorys;
        this.brands = brands;
        this.specs = specs;
    }
}
