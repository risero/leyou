package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/12 21:09
 */
public interface CategoryApi{

    /**
     * 根据多个id查询商品分类
     *
     * @param ids
     * @return
     */
    @GetMapping("/list/ids")
    List<Category> queryByIds(@RequestParam("ids") List<Long> ids);
}
