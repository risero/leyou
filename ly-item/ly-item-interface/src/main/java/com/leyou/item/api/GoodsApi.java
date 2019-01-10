package com.leyou.item.api;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/12 21:05
 */
public interface GoodsApi {

    /**
     * 根据spu的id查询商品详情对象
     *
     * @param spuId 标准商品单元对象id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id") Long spuId);

    /**
     * 根据spu对象的id查询所有sku
     *
     * @param spuId spu对象的id
     * @return
     */
    @GetMapping("/sku/list")
    List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 分页查询SPU对象
     *
     * @param key 搜索的关键字
     * @param saleable 是否上架
     * @param page 当前页
     * @param rows 显示行数
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "saleable", required = false) Boolean saleable,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "5") Integer rows);

    /**
     * 根据spu的id查询spu对象
     *
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    Spu querySpuById(@PathVariable("id") Long spuId);

    /**
     * 根据多个sku的id查询所有的sku
     *
     * @param ids spu对象的id
     * @return
     */
    @GetMapping("/sku/list/ids")
    List<Sku> querySkusBySpuIds(@RequestParam("ids") List<Long> ids);

    /**
     * 减少商品库存
     *
     * @param cartDTOList 购物商品传输对象集合
     * @return
     */
    @PostMapping("/stock/decrease")
    void decreaseStock(@RequestBody List<CartDTO> cartDTOList);
}
