package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.CartDTO;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品货物Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/5 17:04
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询SPU对象
     *
     * @param key 搜索的关键字
     * @param saleable 是否可售
     * @param page 当前页
     * @param rows 显示行数
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "saleable", required = false) Boolean saleable,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "5") Integer rows){
        PageResult<Spu> pageResult = goodsService.querySpuPage(key, saleable, page ,rows);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 商品保存
     *
     * @param spu 标准商品单元对象
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build(); //创建成功
    }

    /**
     * 修改保存
     *
     * @param spu 标准商品单元对象
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        // NO_CONTENT：没有内容, 修改成功返回
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); //创建成功
    }

    /**
     * 根据spu的id查询商品详情对象
     *
     * @param spuId 标准商品单元对象id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long spuId){
        SpuDetail spuDetail = goodsService.queryDetailById(spuId);
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spu对象的id查询所有sku
     *
     * @param spuId spu对象的id
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id") Long spuId){
        List<Sku> skus = goodsService.querySkusBySpuId(spuId);
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据多个sku的id查询所有的sku
     *
     * @param ids spu对象的id
     * @return
     */
    @GetMapping("/sku/list/ids")
    public ResponseEntity<List<Sku>> querySkusBySpuIds(@RequestParam("ids") List<Long> ids){
        List<Sku> skus = goodsService.querySkusBySpuIds(ids);
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据spu的id查询spu对象
     *
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long spuId){
        Spu spu = goodsService.querySpuById(spuId);
        return ResponseEntity.ok(spu);
    }

    /**
     * 减少商品库存
     *
     * @param cartDTOList 购物商品传输对象集合
     * @return
     */
    @PostMapping("/stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> cartDTOList){
        goodsService.decreaseStock(cartDTOList);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
