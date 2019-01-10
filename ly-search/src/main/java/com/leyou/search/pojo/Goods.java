package com.leyou.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 导入索引库的商品对象
 *
 * 剩下没有标上@Feild注解的SpringData Elasticsearch会自动推断类型
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/12 18:37
 */
@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {

    @Id
    private Long id; //spuId

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌

    //卖点 是通过鼠标停留在图片上显示的卖点，不会有人根据这搜索，所以使用keyword不进行分词
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle; // 卖点

    private Long brandId; // 品牌id
    private Long cid1; // 1级分类id
    private Long cid2; // 2级分类id
    private Long cid3; // 3级分类id
    private Date createTime; // 创建时间，根据"新品"进行过滤
    private Set<Long> price; //价格

    @Field(type = FieldType.Keyword, index = false)
    private String skus; //sku信息的json结构
    private Map<String, Object> specs; // 可搜索的规格参数，key是参数名，value是参数值
}
