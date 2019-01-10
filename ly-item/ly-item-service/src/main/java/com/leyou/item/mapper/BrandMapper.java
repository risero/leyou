package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/1 12:58
 */
public interface BrandMapper extends BaseMapper<Brand> {

    /**
     * 新增商品分类和品牌的中间表数据
     *
     * @param cid
     * @param bid
     * @return
     */
    @Insert("insert into tb_category_brand(category_id, brand_id) values(#{cid}, #{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 根据商品分类id查询商品品牌列表
     *
     * @param cid
     * @return
     */
    @Select("select b.* from tb_brand AS b inner join tb_category_brand AS cb " +
            "ON b.id = cb.brand_id where cb.category_id = #{cid};")
    List<Brand> queryBrandByCategoryId(@Param("cid") Long cid);
}
