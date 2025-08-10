package com.dw.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;
    
    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;
    
    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;
    
    /**
     * 操作类型枚举
     */
    enum OperationType {
        INSERT("新增"),
        UPDATE("修改"),
        DELETE("删除"),
        SELECT("查询"),
        LOGIN("登录"),
        LOGOUT("登出"),
        OTHER("其他");
        
        private final String description;
        
        OperationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}