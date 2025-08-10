package com.dw.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 通用Mapper接口，继承MyBatis-Plus的BaseMapper并扩展功能
 * @param <T> 实体类型
 */
public interface DWMapper<T> extends BaseMapper<T> {

    /**
     * 带条件的批量查询
     * @param ids 主键集合
     * @param queryWrapper 条件构造器
     * @return 实体列表
     */
    List<T> selectBatchIdsWithCondition(Collection<? extends Serializable> ids, Wrapper<T> queryWrapper);

    /**
     * 带条件的分页查询
     * @param page 分页对象
     * @param queryWrapper 条件构造器
     * @return 分页结果
     */
    IPage<T> selectPageWithCondition(IPage<T> page, Wrapper<T> queryWrapper);
}
