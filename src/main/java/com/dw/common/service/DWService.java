package com.dw.common.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface DWService<T> {
    // 新增
    boolean save(T entity);

    // 批量新增（默认批次 500 条）
    boolean saveBatch(Collection<T> entityList);

    // 根据 ID 更新
    boolean updateById(T entity);

    // 根据 ID 删除
    boolean removeById(Serializable id);

    // 批量删除
    boolean removeByIds(Collection<?> ids);

    // 根据 ID 查询
    T getById(Serializable id);

    // 条件查询列表
    List<T> list(Wrapper<T> queryWrapper);

    // 分页查询
    <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper);
}
