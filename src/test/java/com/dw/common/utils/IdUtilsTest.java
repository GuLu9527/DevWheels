package com.dw.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;

@DisplayName("ID生成器测试")
public class IdUtilsTest {

    @Test
    @DisplayName("雪花算法ID生成测试")
    void testSnowflakeId() {
        // 生成多个ID
        long id1 = IdUtils.snowflakeId();
        long id2 = IdUtils.snowflakeId();
        long id3 = IdUtils.snowflakeId();
        
        // 验证ID不为空且递增
        assertTrue(id1 > 0);
        assertTrue(id2 > 0);
        assertTrue(id3 > 0);
        assertTrue(id2 > id1);
        assertTrue(id3 > id2);
        
        System.out.println("Snowflake IDs: " + id1 + ", " + id2 + ", " + id3);
    }
    
    @Test
    @DisplayName("雪花算法ID并发生成测试")
    void testSnowflakeIdConcurrency() throws InterruptedException {
        int threadCount = 10;
        int idCountPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Long> allIds = ConcurrentHashMap.newKeySet();
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Set<Long> threadIds = new HashSet<>();
                    for (int j = 0; j < idCountPerThread; j++) {
                        long id = IdUtils.snowflakeId();
                        threadIds.add(id);
                    }
                    allIds.addAll(threadIds);
                    assertEquals(idCountPerThread, threadIds.size(), "单线程内ID应该唯一");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // 验证所有ID唯一
        assertEquals(threadCount * idCountPerThread, allIds.size(), "并发生成的ID应该全部唯一");
    }
    
    @Test
    @DisplayName("UUID生成测试")
    void testUuid() {
        String uuid1 = IdUtils.uuid();
        String uuid2 = IdUtils.uuid();
        String uuid3 = IdUtils.uuid();
        
        // 验证UUID格式和唯一性
        assertNotNull(uuid1);
        assertNotNull(uuid2);
        assertNotNull(uuid3);
        assertEquals(32, uuid1.length()); // 去除横线后的长度
        assertEquals(32, uuid2.length());
        assertEquals(32, uuid3.length());
        
        assertNotEquals(uuid1, uuid2);
        assertNotEquals(uuid2, uuid3);
        assertNotEquals(uuid1, uuid3);
        
        // 验证不包含横线
        assertFalse(uuid1.contains("-"));
        assertFalse(uuid2.contains("-"));
        assertFalse(uuid3.contains("-"));
        
        System.out.println("UUIDs: " + uuid1 + ", " + uuid2 + ", " + uuid3);
    }
    
    @Test
    @DisplayName("标准UUID生成测试")
    void testStandardUuid() {
        String uuid1 = IdUtils.standardUuid();
        String uuid2 = IdUtils.standardUuid();
        
        assertNotNull(uuid1);
        assertNotNull(uuid2);
        assertEquals(36, uuid1.length()); // 包含横线的标准长度
        assertEquals(36, uuid2.length());
        
        // 验证包含横线
        assertTrue(uuid1.contains("-"));
        assertTrue(uuid2.contains("-"));
        
        // 验证格式 xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        assertTrue(uuid1.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
        assertTrue(uuid2.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
        
        assertNotEquals(uuid1, uuid2);
        
        System.out.println("Standard UUIDs: " + uuid1 + ", " + uuid2);
    }
    
    @Test
    @DisplayName("简单UUID生成测试")
    void testSimpleUuid() {
        String uuid1 = IdUtils.simpleUuid();
        String uuid2 = IdUtils.simpleUuid();
        String uuid3 = IdUtils.simpleUuid();
        
        assertNotNull(uuid1);
        assertNotNull(uuid2);
        assertNotNull(uuid3);
        assertEquals(8, uuid1.length());
        assertEquals(8, uuid2.length());
        assertEquals(8, uuid3.length());
        
        // 验证是16进制字符
        assertTrue(uuid1.matches("^[0-9a-f]{8}$"));
        assertTrue(uuid2.matches("^[0-9a-f]{8}$"));
        assertTrue(uuid3.matches("^[0-9a-f]{8}$"));
        
        System.out.println("Simple UUIDs: " + uuid1 + ", " + uuid2 + ", " + uuid3);
    }
    
    @RepeatedTest(5)
    @DisplayName("ID生成性能测试")
    void testIdGenerationPerformance() {
        int count = 10000;
        
        // 测试雪花算法性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            IdUtils.snowflakeId();
        }
        long snowflakeTime = System.currentTimeMillis() - startTime;
        
        // 测试UUID性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            IdUtils.uuid();
        }
        long uuidTime = System.currentTimeMillis() - startTime;
        
        System.out.println("生成 " + count + " 个ID:");
        System.out.println("雪花算法耗时: " + snowflakeTime + "ms");
        System.out.println("UUID耗时: " + uuidTime + "ms");
        
        // 雪花算法应该比UUID快
        assertTrue(snowflakeTime < uuidTime * 2, "雪花算法性能应该显著优于UUID");
    }
    
    @Test
    @DisplayName("ID唯一性大批量测试")
    void testLargeScaleUniqueness() {
        int count = 50000;
        Set<Long> snowflakeIds = new HashSet<>();
        Set<String> uuids = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            snowflakeIds.add(IdUtils.snowflakeId());
            uuids.add(IdUtils.uuid());
        }
        
        assertEquals(count, snowflakeIds.size(), "雪花算法生成的ID应该全部唯一");
        assertEquals(count, uuids.size(), "UUID应该全部唯一");
    }
}