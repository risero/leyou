package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandApi {

    /**
     * 根据id查询商品品牌
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Brand queryById(@PathVariable("id") Long id);

    /**
     * 根据多个id查询商品品牌列表
     *
     * @param ids
     * @return
     */
    @GetMapping("/list")
    List<Brand> queryByIds(@RequestParam("ids") List<Long> ids);
}
