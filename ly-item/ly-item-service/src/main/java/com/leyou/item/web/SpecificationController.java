package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品规格Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/4 20:19
 */
@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询商品规格
     *
     * @param cid 分类id
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据组id或者分类id，查询商品规格参数列表
     *
     * @param gid 组id
     * @param cid 分类id
     * @param searching 是否可搜索
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParamsList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching) {
        List<SpecParam> params = specificationService.queryParamsList(gid, cid, searching);
        return ResponseEntity.ok(params);
    }

    /**
     * 根据分类查询规格组及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("/group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid){
        List<SpecGroup> specGroups = specificationService.queryListByCid(cid);
        return ResponseEntity.ok(specGroups);
    }
}
