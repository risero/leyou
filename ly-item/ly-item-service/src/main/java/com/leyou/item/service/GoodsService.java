package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.CartDTO;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品货物业务
 * <p>
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/5 17:03
 */
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 根据spuId查询Spu对象
     *
     * 一次性查询出商品的所有信息，封装到spu对象中，因为查询完spu还要查询对应的sku。
     * 我们可以在这里一次性查完再返回
     *
     * @param spuId
     * @return
     */
    public Spu querySpuById(Long spuId) {
        // 查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        // 查询sku
        List<Sku> skus = querySkusBySpuId(spuId);

        // 查询detail
        SpuDetail spuDetail = queryDetailById(spuId);

        spu.setSkus(skus);
        spu.setSpuDetail(spuDetail);
        return spu;
    }

    /**
     * 分页查询标准商品单元（SPU）对象
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<Spu> querySpuPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 分页
        PageHelper.startPage(page, rows);

        // 条件过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 关键词过滤
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        // 上下架过滤，是否上架，0下架，1上架
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable == true ? 1 : 0);
        }
        // 默认排序
        example.setOrderByClause("last_update_time DESC");

        // 查询
        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        // 解析分类和品牌的名称
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo pageInfo = new PageInfo(spus);
        PageResult pageResult = new PageResult<Spu>(pageInfo.getTotal(), spus);

        // 返回结果
        return pageResult;
    }

    /**
     * 解析分类和品牌的名称
     *
     * @param spus Spu对象集合
     */
    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            // 处理分类名称
            List<Category> categories = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 转成商品分类名称集合
            List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/")); //把集合的数据使用“/”拼接成字符串

            // 处理品牌名称
            Brand brand = brandService.queryByid(spu.getBrandId());
            spu.setBname(brand.getName());
        }
    }

    /**
     * 保存商品
     *
     * @param spu
     */
    @Transactional
    public void saveGoods(Spu spu) {
        // 新增/修改spu
        if(spu.getId() == null){
            spu.setId(null); //设置为null然id自增长
            spu.setCreateTime(new Date());
            spu.setLastUpdateTime(spu.getCreateTime());
            spu.setSaleable(true);
            spu.setValid(false); // 是否删除，false不删除
            int count = spuMapper.insert(spu);
            if (count != 1) {
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            // 发送RabbitMQ消息
            amqpTemplate.convertAndSend("item.insert", spu.getId());
        }

        // 新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int spuDetailCount = spuDetailMapper.insert(spuDetail);
        if (spuDetailCount != 1) {
            new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        // 新增sku对象和stock对象
        saveSkuAndStock(spu);
    }

    private void saveSkuAndStock(Spu spu){
        // 库存集合
        List<Stock> stockList = new ArrayList<>();
        // 新增skus
        for (Sku sku : spu.getSkus()) {
            sku.setId(null);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            sku.setSpuId(spu.getId());
            int skuCount = skuMapper.insert(sku);
            if (skuCount != 1) {
                new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }

            // 新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        // 批量新增库存
        int stockCount = stockMapper.insertList(stockList);
        if (stockCount != 1) {
            new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    /**
     * 根据spu的id查询商品详情对象
     *
     * @param spuId 标准商品单元对象id
     * @return
     */
    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail == null) {
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    /**
     * 根据spu对象的id查询所有sku
     *
     * @param spuId spu对象的id
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        // 查询sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }

        // 查询库存
        List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
        List<Sku> skuList = loadStockInSku(ids, skus);
        return skuList;
    }

    /**
     * 修改商品
     *
     * spu数据可以修改，但是sku数据无法修改，因为有可能之前存在的sku现在已经不存在了，
     * 或者以前的sku属性都不存在了。比如以前内存有4G，现在没了。
     *
     * @param spu
     */
    @Transactional
    public void updateGoods(Spu spu) {
        // 判断spu对象id是否为null
        if(spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_ID_CANNOT_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId()); // 感觉spu的id删除sku对象，一个spu对应多个sku对象
        // 先查询以前的sku
        List<Sku> skuList = skuMapper.select(sku);
        if(! CollectionUtils.isEmpty(skuList)){
           // sku存在，则删除sku
            skuMapper.delete(sku);
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            // 删除stock库存
            stockMapper.deleteByIdList(ids);
        }
        // 修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        // 修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if(count != 1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }

        // 新增sku和stock
        saveSkuAndStock(spu);

        // 发送RabbitMQ消息
        amqpTemplate.convertAndSend("item.update", spu.getId());
    }

    /**
     *  根据多个sku的id查询多个sku
     *
     * @param ids
     * @return
     */
    public List<Sku> querySkusBySpuIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(skus)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        loadStockInSku(ids, skus);
        return loadStockInSku(ids, skus);

    }

    /**
     * 加载sku库存
     *
     * @param ids
     * @param skus
     * @return
     */
    private List<Sku> loadStockInSku(List<Long> ids, List<Sku> skus) {
        // 查询库存
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
        }
        // 把List转换成Map集合，我们把stock变成一个map的key：sku的id，map的vlaue：库存的值
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        // 根据map的key：skuId，获取库存的值，设置到sku对象中
        skus.forEach(s -> s.setStock(stockMap.get(s.getId())));
        return skus;
    }

    /**
     * 减少商品库存
     *
     * @param cartDTOList
     */
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        // 遍历购买的商品
        for (CartDTO cartDTO : cartDTOList) {
            // 减少商品库存，自定义sql，条件如果库存 >= 1则进行修改
            int count = stockMapper.descreaseStock(cartDTO.getSkuId(), cartDTO.getNum());
            // 库存不足抛异常
            if(count != 1){
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }
        }
    }
}
