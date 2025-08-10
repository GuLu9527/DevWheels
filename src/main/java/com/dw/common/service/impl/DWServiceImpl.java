package com.dw.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.common.mapper.DWMapper;
import com.dw.common.service.DWService;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 通用Service实现类，继承MyBatis-Plus的ServiceImpl
 * @param <M> Mapper类型
 * @param <T> 实体类型
 */
public class DWServiceImpl<M extends DWMapper<T>, T> extends ServiceImpl<M, T> implements DWService<T> {

    /**
     * 新增实体（带事务）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T entity) {
        return super.save(entity);
    }

    /**
     * 批量新增（每批500条，带事务）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList) {
        return super.saveBatch(entityList, 500);
    }

    /**
     * 根据ID更新（带事务）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T entity) {
        return super.updateById(entity);
    }

    /**
     * 根据ID删除（带事务）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * 批量删除（带事务）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> ids) {
        return super.removeByIds(ids);
    }

    /**
     * 根据ID查询
     */
    @Override
    public T getById(Serializable id) {
        return super.getById(id);
    }

    /**
     * 条件查询列表
     */
    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return super.list(queryWrapper);
    }

    /**
     * 分页查询
     */
    @Override
    public <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
        return super.page(page, queryWrapper);
    }
}
