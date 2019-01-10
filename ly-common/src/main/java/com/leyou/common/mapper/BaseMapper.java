package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/7 20:50
 */
@RegisterMapper // 在通用mapper对象中注册该自定义注解
public interface BaseMapper<T> extends Mapper<T>, InsertListMapper<T>, IdListMapper<T, Long>{
}
