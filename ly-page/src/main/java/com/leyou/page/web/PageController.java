package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/19 19:45
 */
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model){
        // 查询模型数据
        Map<String, Object> attributes = pageService.loadModel(spuId);
        // 准备模型数据
        model.addAllAttributes(attributes);
        // 返回模型视图
        return "item";
    }
}
