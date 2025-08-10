package com.dw.common.utils;

import java.util.UUID;

/**
 * ID生成工具类
 */
public class IdUtils {
    
    private static final SnowflakeIdGenerator SNOWFLAKE = new SnowflakeIdGenerator(1, 1);
    
    /**
     * 生成雪花ID
     */
    public static long snowflakeId() {
        return SNOWFLAKE.nextId();
    }
    
    /**
     * 生成UUID（去除横线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成标准UUID
     */
    public static String standardUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成简单UUID（前8位）
     */
    public static String simpleUuid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}