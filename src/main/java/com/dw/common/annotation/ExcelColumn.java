package com.dw.common.annotation;

import java.lang.annotation.*;

/**
 * Excel列注解
 * 基于EasyExcel的ExcelProperty注解进行扩展
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
    
    /**
     * 列名
     */
    String value() default "";
    
    /**
     * 列索引
     */
    int index() default -1;
    
    /**
     * 是否必填
     */
    boolean required() default false;
    
    /**
     * 最大长度
     */
    int maxLength() default -1;
    
    /**
     * 数据格式说明
     */
    String format() default "";
    
    /**
     * 示例值
     */
    String example() default "";
    
    /**
     * 备注
     */
    String remark() default "";
}