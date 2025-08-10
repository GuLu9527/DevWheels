package com.dw.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统操作日志
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
public class SysOperationLog extends DWEntity {
    
    /**
     * 操作用户ID
     */
    private Long userId;
    
    /**
     * 操作用户名
     */
    private String username;
    
    /**
     * 操作描述
     */
    private String operation;
    
    /**
     * 操作类型
     */
    private String operationType;
    
    /**
     * 请求方法
     */
    private String method;
    
    /**
     * 请求URI
     */
    private String uri;
    
    /**
     * 请求IP
     */
    private String ip;
    
    /**
     * 请求参数
     */
    private String params;
    
    /**
     * 返回结果
     */
    private String result;
    
    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 异常信息
     */
    private String errorMsg;
}