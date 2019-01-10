package com.leyou.search.web;

import com.leyou.common.vo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品搜索Controller
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/15 14:51
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 商品搜索分页查询
     * @RequestBody注解：接收请求的json数据
     *
     * @param request 搜索查询对象
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
        PageResult<Goods> pageResult = searchService.search(request);
        return ResponseEntity.ok(pageResult);
    }
}
