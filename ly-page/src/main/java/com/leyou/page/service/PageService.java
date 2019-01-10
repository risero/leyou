package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品详情页业务
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/20 17:06
 */
@Slf4j
@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 加载商品详情页数据
     *
     * @param spuId
     * @return
     */
    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        // 查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        List<Sku> skus = spu.getSkus();

        // 查询detail详情
        SpuDetail detail = spu.getSpuDetail();
        // 查询brand
        Brand brand = brandClient.queryById(spu.getBrandId());
        // 查询商品分类
        List<Category> categorys = categoryClient.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid2()));

        // 查询规格参数
        List<SpecGroup> specGroups = specificationClient.queryGroupByCid(spu.getCid3());

        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("skus", skus);
        model.put("categorys", categorys);
        model.put("detail", detail);
        model.put("brand", brand);
        model.put("specs", specGroups);

        return model;
    }

    /**
     * 生成商品详情静态页
     *
     * @param spuId
     */
    public void createHtml(Long spuId){
        // 上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        // 输出流
        File desc = new File("C:\\Users\\dell\\Desktop\\Thymeleaf", spuId + ".html");
        if(desc.exists()){ // 该文件已存在
            desc.delete(); // 存在就删除掉
        }
        // 生成静态页
        try(Writer writer = new PrintWriter(desc, "UTF-8")){
            // 生成Html
            templateEngine.process("item", context, writer );
        } catch (Exception e) {
            log.error("[静态页服务] 生成静态页异常！", e);
        }
    }

    /**
     * 删除商品详情页
     *
     * @param spuId
     */
    public void deleteHtml(Long spuId) {
        File desc = new File("C:\\Users\\dell\\Desktop\\Thymeleaf", spuId + ".html");
        if(desc.exists()){
            desc.delete(); // 删除静态页
        }
    }
}
