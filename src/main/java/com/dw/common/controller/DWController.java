package com.dw.common.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dw.common.result.DWResult;
import com.dw.common.service.DWService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * 通用Controller，提供基础CRUD接口
 * @param <T> 实体类型
 */
public abstract class DWController<T> {

    /**
     * 注入通用Service（由子类自动继承）
     */
    @Autowired
    protected DWService<T> baseService;

    /**
     * 新增接口
     * @param entity 实体对象（带参数校验）
     */
    @PostMapping
    public DWResult<?> save(@Validated @RequestBody T entity) {
        boolean success = baseService.save(entity);
        return success ? DWResult.success() : DWResult.fail("新增失败");
    }

    /**
     * 更新接口
     * @param entity 实体对象（带参数校验）
     */
    @PutMapping
    public DWResult<?> update(@Validated @RequestBody T entity) {
        boolean success = baseService.updateById(entity);
        return success ? DWResult.success() : DWResult.fail("更新失败");
    }

    /**
     * 根据ID删除
     * @param id 主键ID
     */
    @DeleteMapping("/{id}")
    public DWResult<?> remove(@PathVariable Serializable id) {
        boolean success = baseService.removeById(id);
        return success ? DWResult.success() : DWResult.fail("删除失败");
    }

    /**
     * 根据ID查询
     * @param id 主键ID
     */
    @GetMapping("/{id}")
    public DWResult<T> getById(@PathVariable Serializable id) {
        T entity = baseService.getById(id);
        return entity != null ? DWResult.success(entity) : DWResult.fail("数据不存在");
    }

    /**
     * 分页查询
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param entity 查询条件（非空字段作为查询条件）
     */
    @GetMapping("/page")
    public DWResult<IPage<T>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            T entity) {
        IPage<T> page = new Page<>(pageNum, pageSize);
        Wrapper<T> queryWrapper = Wrappers.query(entity);
        IPage<T> resultPage = baseService.page(page, queryWrapper);
        return DWResult.success(resultPage);
    }

    /**
     * 列表查询（不带分页）
     * @param entity 查询条件
     */
    @GetMapping("/list")
    public DWResult<List<T>> list(T entity) {
        Wrapper<T> queryWrapper = Wrappers.query(entity);
        List<T> list = baseService.list(queryWrapper);
        return DWResult.success(list);
    }
}
