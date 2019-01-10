package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品规格业务
 *
 * author 暗氵愧
 * HostName dell
 * Date 2018/12/4 20:18
 */
@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询规格组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        // 查询条件
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        // 根据非空字段执行查询
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return specGroups;
    }

    /**
     * 根据组id查询商品规格参数
     *
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    public List<SpecParam> queryParamsList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        // 条件可能为null，如果为null就不会根据该条件进行查询
        List<SpecParam> specParams = specParamMapper.select(specParam);
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> specGroups = queryGroupsByCid(cid);

        // 查询当前分类下的规格参数
        List<SpecParam> specParams = queryParamsList(null, cid, null);
        // 先把规格参数变为map，map.key是规格组的id，map.value的值是组下的所有参数
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam specParam : specParams) {
            // 这个组id在map中不存在，新增一个List
            List<SpecParam> paramList = null;
            if(! map.containsKey(specParam.getGroupId())){
                paramList = new ArrayList<>();
                map.put(specParam.getGroupId(), paramList);
            }
            // 把所有Param填充在map的value中
            paramList = map.get(specParam.getGroupId());
            paramList.add(specParam);
        }

        // 填充param列表到group中
        for (SpecGroup specGroup : specGroups) {
            // 根据组的id获取规格参数
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
