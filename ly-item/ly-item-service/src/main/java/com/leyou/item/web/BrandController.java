package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品品牌Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/1 13:00
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     *
     * @param page 当前页
     * @param rows 每行显示数据
     * @param key 关键字
     * @param sortBy 排序字段
     * @param desc 是否排序
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc){
        PageResult<Brand> pageResult = brandService.queryBrandByPage(page, rows, key, sortBy, desc);
        if(pageResult == null){
            ResponseEntity.status(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增品牌
     * Void 是无返回的意思
     *
     * @param brand 品牌对象
     * @param cids 多个品牌id
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build(); //无返回值使用 build()
    }

    /**
     * 根据分类id查询品牌列表
     *
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid){
        List<Brand> brands = brandService.queryBrandByCid(cid);
        return ResponseEntity.ok(brands);
    }

    /**
     * 根据id查询商品品牌
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id){
        Brand brand = brandService.queryByid(id);
        return ResponseEntity.ok(brand);
    }

    /**
     * 根据多个id查询商品品牌列表
     *
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Brand>> queryByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> brands = brandService.queryByIds(ids);
        return ResponseEntity.ok(brands);
    }
}
