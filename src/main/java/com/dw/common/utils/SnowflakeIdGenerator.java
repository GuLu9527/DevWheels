package com.dw.common.utils;

/**
 * 雪花算法ID生成器
 * 64位ID = 1位符号位 + 41位时间戳 + 10位机器ID(5位数据中心ID + 5位工作机器ID) + 12位序列号
 */
public class SnowflakeIdGenerator {
    
    // 开始时间戳 (2024-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1704067200000L;
    
    // 各部分位数
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;
    
    // 最大值
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    // 位移量
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    
    private final long datacenterId;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    /**
     * 构造函数
     * @param datacenterId 数据中心ID (0~31)
     * @param workerId 工作机器ID (0~31)
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }
    
    /**
     * 获取下一个ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        
        // 上次生成ID的时间戳
        lastTimestamp = timestamp;
        
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
    
    /**
     * 返回以毫秒为单位的当前时间
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}