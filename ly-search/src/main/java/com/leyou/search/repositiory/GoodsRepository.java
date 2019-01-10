package com.leyou.search.repositiory;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 通用商品搜索接口
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/13 17:26
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
