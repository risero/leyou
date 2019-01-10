package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

// IdListMapper<对象类型, 主键类型> 根据多个id进行查询或者删除操作
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category, Long> {

}
