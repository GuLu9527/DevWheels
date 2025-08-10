package com.dw.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

/**
 * 测试基类
 * 提供通用的测试配置和工具方法
 */
@ActiveProfiles("test")
@DisplayName("基础测试类")
public abstract class BaseTest {

    protected LocalDateTime testStartTime;

    @BeforeEach
    void setUpBase() {
        testStartTime = LocalDateTime.now();
        System.out.println("=== 测试开始: " + this.getClass().getSimpleName() + " ===");
    }

    @AfterEach
    void tearDownBase() {
        LocalDateTime testEndTime = LocalDateTime.now();
        long duration = java.time.Duration.between(testStartTime, testEndTime).toMillis();
        System.out.println("=== 测试结束: " + this.getClass().getSimpleName() + 
                          " (耗时: " + duration + "ms) ===");
    }

    /**
     * 创建测试用户数据
     */
    protected TestUser createTestUser(String name, int age, String email) {
        return new TestUser(name, age, email, LocalDateTime.now());
    }

    /**
     * 创建测试产品数据
     */
    protected TestProduct createTestProduct(String name, double price, int stock) {
        return new TestProduct(name, price, stock, LocalDateTime.now());
    }

    /**
     * 等待指定毫秒数
     */
    protected void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 断言执行时间在指定范围内
     */
    protected void assertExecutionTime(Runnable task, long minMs, long maxMs) {
        long startTime = System.currentTimeMillis();
        task.run();
        long endTime = System.currentTimeMillis();
        long actualTime = endTime - startTime;
        
        if (actualTime < minMs || actualTime > maxMs) {
            throw new AssertionError(String.format(
                "执行时间 %dms 不在期望范围 [%dms, %dms] 内", 
                actualTime, minMs, maxMs));
        }
    }

    // 测试数据类
    public static class TestUser {
        private String name;
        private int age;
        private String email;
        private LocalDateTime createTime;

        public TestUser(String name, int age, String email, LocalDateTime createTime) {
            this.name = name;
            this.age = age;
            this.email = email;
            this.createTime = createTime;
        }

        // Getters
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }
        public LocalDateTime getCreateTime() { return createTime; }
    }

    public static class TestProduct {
        private String name;
        private double price;
        private int stock;
        private LocalDateTime createTime;

        public TestProduct(String name, double price, int stock, LocalDateTime createTime) {
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.createTime = createTime;
        }

        // Getters
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public LocalDateTime getCreateTime() { return createTime; }
    }
}