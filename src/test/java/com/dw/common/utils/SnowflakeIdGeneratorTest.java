package com.dw.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DisplayName("雪花算法生成器测试")
public class SnowflakeIdGeneratorTest {

    @Test
    @DisplayName("构造函数参数验证测试")
    void testConstructorValidation() {
        // 正常构造
        assertDoesNotThrow(() -> new SnowflakeIdGenerator(1, 1));
        assertDoesNotThrow(() -> new SnowflakeIdGenerator(0, 0));
        assertDoesNotThrow(() -> new SnowflakeIdGenerator(31, 31));
        
        // 异常构造 - 数据中心ID超出范围
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(32, 1));
        
        // 异常构造 - 工作机器ID超出范围
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1, -1));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1, 32));
    }
    
    @Test
    @DisplayName("ID生成基础功能测试")
    void testBasicIdGeneration() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        long id3 = generator.nextId();
        
        assertTrue(id1 > 0);
        assertTrue(id2 > 0);
        assertTrue(id3 > 0);
        
        // ID应该递增
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
        
        System.out.println("Generated IDs: " + id1 + ", " + id2 + ", " + id3);
    }
    
    @Test
    @DisplayName("不同机器ID生成器测试")
    void testDifferentMachineIds() {
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(0, 0);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(0, 1);
        SnowflakeIdGenerator generator3 = new SnowflakeIdGenerator(1, 0);
        
        long id1 = generator1.nextId();
        long id2 = generator2.nextId();
        long id3 = generator3.nextId();
        
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
        
        System.out.println("Different machine IDs: " + id1 + ", " + id2 + ", " + id3);
    }
    
    @Test
    @DisplayName("高并发ID生成测试")
    void testConcurrentIdGeneration() throws InterruptedException {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        int threadCount = 20;
        int idCountPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Long> allIds = new HashSet<>();
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Set<Long> threadIds = new HashSet<>();
                    for (int j = 0; j < idCountPerThread; j++) {
                        long id = generator.nextId();
                        threadIds.add(id);
                    }
                    
                    synchronized (allIds) {
                        allIds.addAll(threadIds);
                    }
                    
                    assertEquals(idCountPerThread, threadIds.size());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // 验证所有ID唯一
        assertEquals(threadCount * idCountPerThread, allIds.size());
        System.out.println("并发生成 " + allIds.size() + " 个唯一ID");
    }
    
    @Test
    @DisplayName("ID格式解析测试")
    void testIdFormat() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(5, 10);
        long id = generator.nextId();
        
        // 验证ID为正数
        assertTrue(id > 0);
        
        // 验证ID位数（雪花算法生成的ID通常是64位long的正数部分）
        String idBinary = Long.toBinaryString(id);
        assertTrue(idBinary.length() <= 63); // 不超过63位（去掉符号位）
        
        System.out.println("Generated ID: " + id);
        System.out.println("Binary format: " + idBinary);
        System.out.println("ID length in bits: " + idBinary.length());
    }
    
    @Test
    @DisplayName("大量ID生成唯一性测试")
    void testLargeVolumeUniqueness() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        int count = 100000;
        Set<Long> ids = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            long id = generator.nextId();
            assertTrue(ids.add(id), "ID应该唯一: " + id);
        }
        
        assertEquals(count, ids.size());
        System.out.println("成功生成 " + count + " 个唯一ID");
    }
    
    @Test
    @DisplayName("性能基准测试")
    void testPerformanceBenchmark() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
        int warmupCount = 10000;
        int testCount = 100000;
        
        // 预热
        for (int i = 0; i < warmupCount; i++) {
            generator.nextId();
        }
        
        // 性能测试
        long startTime = System.nanoTime();
        for (int i = 0; i < testCount; i++) {
            generator.nextId();
        }
        long endTime = System.nanoTime();
        
        long totalTime = endTime - startTime;
        double avgTime = totalTime / (double) testCount;
        double idsPerSecond = 1_000_000_000.0 / avgTime;
        
        System.out.println("生成 " + testCount + " 个ID:");
        System.out.println("总耗时: " + totalTime / 1_000_000.0 + " ms");
        System.out.println("平均每个ID耗时: " + avgTime + " ns");
        System.out.println("每秒生成ID数: " + String.format("%.0f", idsPerSecond));
        
        // 性能要求：每秒至少生成100万个ID
        assertTrue(idsPerSecond > 1_000_000, "性能应该达到每秒100万个ID以上");
    }
}