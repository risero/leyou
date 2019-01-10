package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repositiory.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/13 18:13
 */
@Slf4j
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    /**
     * 构建goods对象
     *
     * @param spu
     * @return
     */
    public Goods buildCoods(Spu spu){
        Long spuId = spu.getId();
        // 根据多个id查询商品分类
        List<Category> categories = categoryClient.queryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 这里其实可以不用抛异常，因为找不到本来就会抛异常，Controller层也会帮我们处理
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> brandNames = categories.stream().map(Category::getName).collect(Collectors.toList());

        // 查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        // 搜索字段，拼接json字符串，使用空格做分割
        String all = spu.getSubTitle() + StringUtils.join(brandNames, " ") + brand.getName();

        // 查询sku
        List<Sku> skuList = goodsClient.querySkusBySpuId(spuId);
        if(CollectionUtils.isEmpty(skuList)){
            System.err.println(spuId + "找不到对应的sku对象");
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }

        // 对sku进行处理
        List<Map<String, Object>> skus = new ArrayList<>();
        // 价格集合
        Set<Long> skuPrices = new HashSet<>();

        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            // 截取第一个以","逗号分割之前的字符串
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            map.put("price", sku.getPrice());
            skus.add(map);
            skuPrices.add(sku.getPrice());
        }

        // 查询规格参数，查询的结果只有参数的key，没有值。需要查询商品详情才有参数对应的值
        List<SpecParam> params = specificationClient.queryParamsList(null, spu.getCid3(), true);
        if(CollectionUtils.isEmpty(params)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        // 查询商品详情,规格参数的value
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        if(spuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }

        // 获取通用规格参数
        String genericSpecJson = spuDetail.getGenericSpec();
        Map<Long, String> genericSpecMap = JsonUtils.parseMap(genericSpecJson, Long.class, String.class);

        // 获取特有规格参数
        String specialSpecJson = spuDetail.getSpecialSpec();
        Map<Long, List<String>> specialSpecMap = JsonUtils
                .nativeRead(specialSpecJson, new TypeReference<Map<Long, List<String>>>() {});

        // 规格参数，key是规格参数的名称，value是规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            String key = param.getName(); // 规格名称
            Object value = "";                // 规格的值
            // 判断规格参数是否是通用规格的值
            if(param.getGeneric()){
                // 值是规格参数的id，通过id来获取值
                value = genericSpecMap.get(param.getId());
                // 是否是数值类型
                if(param.getNumeric()){
                    // 处理分段
                    // value = chooseSegment(value.toString(), param);
                }
            }else {
                value = specialSpecMap.get(param.getId());
            }
            // 存入map
            specs.put(key, value);
        }

        // 创建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spuId);
        goods.setSubTitle(spu.getSubTitle());

        goods.setAll(all); //  搜索字段，包含标题，分类，品牌，规格等
        goods.setPrice(skuPrices); //  所有Sku的价格集合
        goods.setSkus(JsonUtils.serialize(skus)); // 根据spuId查询的所有的Sku对象 json字符串
        goods.setSpecs(specs); //  所有的可搜索的规格参数
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 商品搜索分页查询
     *
     * @param request
     * @return
     */
    public PageResult<Goods> search(SearchRequest request) {
        Integer page = request.getPage() - 1; //因为默认从0开始才是第一页，不设置的话永远都查不到第一页
        Integer size = request.getSize();

        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        // 分页
        queryBuilder.withPageable(PageRequest.of(page, size));

        // 搜索条件
        QueryBuilder basicQuery = buildBasicQuery(request);

        // 过滤，QueryBuilders对象中提供了非常多的查询方法，使用匹配查询查找关键词
        queryBuilder.withQuery(basicQuery);

        // 3.聚合分类和品牌
        // 3.1聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));

        // 3.1聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 构建查询对象
        NativeSearchQuery searchQuery = queryBuilder.build();

        // 查询，查询后也有分页等信息
        AggregatedPage<Goods> result = template.queryForPage(searchQuery, Goods.class);

        // 解析结果
        // 解析分页结果
        long total = result.getTotalElements();
        int totalPage = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        // 解析聚合结果
        Aggregations aggregations = result.getAggregations();
        List<Category> categorys = parseCategoryAgg(aggregations.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggregations.get(brandAggName));

        // 6.完成规格参数聚合
        List<Map<String, Object>> specs = null;
        if(categorys != null && categorys.size() == 1){
            // 商品分类存在并且数量为1，可以聚合规格参数，对第1个商品分类进行聚合
            specs = buildSpecificationAgg(categorys.get(0).getId(), basicQuery);
            if(CollectionUtils.isEmpty(specs)){
                throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
            }
        }
        // 封装返回结果
        return new SearchResult(total, totalPage, goodsList, categorys, brands, specs);
    }

    /**
     * 处理搜索条件
     *
     * @param request
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest request) {
        // 创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        // 过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            // 处理key，因为品牌和分类传入的key都是id，所以我们要拼接key的条件
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key = "specs."+ key +".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key, value));
        }
        return queryBuilder;
    }

    /**
     * 构建商品规格参数
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        // 1、查询需要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamsList(null, cid, true);
        // 2、聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 2.1、带上原来的查询条件
        queryBuilder.withQuery(basicQuery);

        // 2.2、聚合，遍历添加多个聚合
        for (SpecParam param : params) {
            String name = param.getName();
            NativeSearchQueryBuilder addAggregation = queryBuilder
                    .addAggregation(AggregationBuilders.terms(name).field("specs."+ name +".keyword"));
        }
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        // 3、获取结果，获取所有列表中聚合的结果
        Aggregations aggregations = result.getAggregations();
        for (SpecParam param : params) {
            // 规格参数名称
            String name = param.getName();
            StringTerms terms = aggregations.get(name);
            Map<String, Object> map = new HashMap<>();

            List<String> options = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            // 准备map
            map.put("key", name);
            map.put("options", options);
            specs.add(map);
        }
        // 4、解析结果
        return specs;
    }

    /**
     * 解析商品品牌聚合
     *
     * @param terms
     * @return
     */
    private List<Brand> parseBrandAgg(LongTerms terms) {
        try{
            // 先把集合转成Long类型的流，再通过collector转为Long类型集合
            List<Long> ids = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryByIds(ids);
            return brands;
        } catch (Exception e){
            log.error("[搜索服务]查询商品异常：", e);
            return null;
        }
    }

    /**
     * 解析商品分类聚合
     *
     * @param terms
     * @return
     */
    private List<Category> parseCategoryAgg(LongTerms terms) {
        try{
            List<Long> ids = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryByIds(ids);
            return categories;
        }catch (Exception e){
            log.error("[搜索服务]查询分类异常：", e);
            return null;
        }
    }

    /**
     * 新增或者修改商品
     * 这里不处理异常，如果发生异常spring会捕获到，调用者不执行，消息就会回滚
     *
     * @param spuId
     */
    public void createOrUpdate(Long spuId) {
        // 查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        // 构建goods对象
        Goods goods = buildCoods(spu);
        // 存入索引库
        goodsRepository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);

    }
}
