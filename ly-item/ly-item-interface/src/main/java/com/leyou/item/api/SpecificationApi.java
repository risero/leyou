package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {

    /**
     * 根据组id或者分类id，查询商品规格参数列表
     *
     * @param gid 组id
     * @param cid 分类id
     * @param searching 是否可搜索
     * @return
     */
    @GetMapping("/params")
    List<SpecParam> queryParamsList(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching);

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    @GetMapping("/group")
    List<SpecGroup> queryGroupByCid(@RequestParam("cid") Long cid);
}
