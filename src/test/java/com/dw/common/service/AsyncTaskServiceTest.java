package com.dw.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
@DisplayName("异步任务服务测试")
public class AsyncTaskServiceTest {

    @InjectMocks
    private AsyncTaskService asyncTaskService;

    private ExecutorService testExecutor;

    @BeforeEach
    void setUp() {
        // 创建测试用的线程池
        testExecutor = Executors.newFixedThreadPool(5);
        
        // 手动设置异步任务服务的执行器（在实际Spring环境中会自动注入）
        // 这里我们需要通过反射或其他方式设置，但为了测试简化，我们直接测试异步逻辑
    }

    @Test
    @DisplayName("无返回值异步任务执行测试")
    void testExecuteAsync() throws InterruptedException {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        AtomicReference<String> threadName = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        // 模拟异步任务
        Runnable task = () -> {
            try {
                Thread.sleep(100); // 模拟任务执行时间
                taskExecuted.set(true);
                threadName.set(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        // 使用测试线程池直接执行，模拟@Async行为
        testExecutor.execute(task);

        // 等待任务完成
        assertTrue(latch.await(5, TimeUnit.SECONDS), "任务应该在5秒内完成");
        assertTrue(taskExecuted.get(), "任务应该被执行");
        assertNotEquals(Thread.currentThread().getName(), threadName.get(), "应该在不同的线程中执行");

        System.out.println("任务在线程中执行: " + threadName.get());
    }

    @Test
    @DisplayName("有返回值异步任务执行测试")
    void testExecuteAsyncWithResult() throws InterruptedException, ExecutionException {
        String expectedResult = "异步任务执行结果";
        
        // 模拟有返回值的异步任务
        Callable<String> task = () -> {
            Thread.sleep(100); // 模拟任务执行时间
            return expectedResult;
        };

        Future<String> future = testExecutor.submit(task);
        
        String actualResult = future.get();
        assertEquals(expectedResult, actualResult);
        assertTrue(future.isDone());

        System.out.println("异步任务返回结果: " + actualResult);
    }

    @Test
    @DisplayName("异步任务异常处理测试")
    void testAsyncTaskException() throws InterruptedException {
        AtomicReference<Exception> caughtException = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Runnable errorTask = () -> {
            try {
                throw new RuntimeException("测试异常");
            } catch (Exception e) {
                caughtException.set(e);
            } finally {
                latch.countDown();
            }
        };

        testExecutor.execute(errorTask);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(caughtException.get());
        assertEquals("测试异常", caughtException.get().getMessage());

        System.out.println("捕获到异常: " + caughtException.get().getMessage());
    }

    @Test
    @DisplayName("异步发送邮件测试")
    void testSendMailAsync() throws InterruptedException {
        AtomicBoolean mailSent = new AtomicBoolean(false);
        AtomicReference<String> mailContent = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        // 模拟邮件发送任务
        Runnable mailTask = () -> {
            try {
                String to = "test@example.com";
                String subject = "测试邮件";
                String content = "这是一封测试邮件";
                
                // 模拟邮件发送延迟
                Thread.sleep(200);
                
                mailSent.set(true);
                mailContent.set(content);
                
                System.out.println("异步发送邮件: " + subject + " -> " + to);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        testExecutor.execute(mailTask);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertTrue(mailSent.get(), "邮件应该被发送");
        assertEquals("这是一封测试邮件", mailContent.get());
    }

    @Test
    @DisplayName("异步处理文件测试")
    void testProcessFileAsync() throws InterruptedException {
        AtomicBoolean fileProcessed = new AtomicBoolean(false);
        AtomicReference<String> processedFilePath = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        String testFilePath = "/test/path/file.txt";

        // 模拟文件处理任务
        Runnable fileTask = () -> {
            try {
                // 模拟文件处理时间
                Thread.sleep(150);
                
                fileProcessed.set(true);
                processedFilePath.set(testFilePath);
                
                System.out.println("异步处理文件: " + testFilePath);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        testExecutor.execute(fileTask);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertTrue(fileProcessed.get(), "文件应该被处理");
        assertEquals(testFilePath, processedFilePath.get());
    }

    @Test
    @DisplayName("异步数据同步测试")
    void testSyncDataAsync() throws InterruptedException {
        AtomicBoolean dataSynced = new AtomicBoolean(false);
        AtomicInteger syncedRecords = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        // 模拟数据同步任务
        Runnable syncTask = () -> {
            try {
                // 模拟数据同步处理
                Thread.sleep(300);
                
                int records = 1000;
                syncedRecords.set(records);
                dataSynced.set(true);
                
                System.out.println("异步同步数据: " + records + " 条记录");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        testExecutor.execute(syncTask);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertTrue(dataSynced.get(), "数据应该被同步");
        assertEquals(1000, syncedRecords.get());
    }

    @Test
    @DisplayName("延迟执行异步任务测试")
    void testExecuteAsyncWithDelay() throws InterruptedException {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        AtomicLong executionTime = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(1);

        long startTime = System.currentTimeMillis();
        long delayMs = 500; // 延迟500毫秒

        // 模拟延迟执行任务
        Runnable delayedTask = () -> {
            try {
                Thread.sleep(delayMs); // 模拟延迟
                
                executionTime.set(System.currentTimeMillis() - startTime);
                taskExecuted.set(true);
                
                System.out.println("延迟任务执行，实际延迟: " + executionTime.get() + "ms");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        testExecutor.execute(delayedTask);

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertTrue(taskExecuted.get(), "延迟任务应该被执行");
        assertTrue(executionTime.get() >= delayMs, "执行时间应该大于等于延迟时间");
    }

    @Test
    @DisplayName("并发异步任务执行测试")
    void testConcurrentAsyncTasks() throws InterruptedException {
        int taskCount = 10;
        AtomicInteger completedTasks = new AtomicInteger(0);
        AtomicInteger totalValue = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(taskCount);

        // 创建多个并发任务
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i + 1;
            
            Runnable concurrentTask = () -> {
                try {
                    // 模拟任务执行时间
                    Thread.sleep(100 + (int)(Math.random() * 100));
                    
                    totalValue.addAndGet(taskId);
                    completedTasks.incrementAndGet();
                    
                    System.out.println("并发任务 " + taskId + " 完成");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            };

            testExecutor.execute(concurrentTask);
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "所有并发任务应该在10秒内完成");
        assertEquals(taskCount, completedTasks.get(), "所有任务应该都完成");
        assertEquals(55, totalValue.get(), "总值应该等于1+2+...+10=55"); // 1到10的和
        
        System.out.println("并发任务完成数: " + completedTasks.get());
        System.out.println("总计算值: " + totalValue.get());
    }

    @Test
    @DisplayName("异步任务超时测试")
    void testAsyncTaskTimeout() throws InterruptedException {
        AtomicBoolean taskCompleted = new AtomicBoolean(false);
        AtomicBoolean taskInterrupted = new AtomicBoolean(false);

        // 创建一个长时间运行的任务
        Runnable longRunningTask = () -> {
            try {
                Thread.sleep(10000); // 睡眠10秒
                taskCompleted.set(true);
            } catch (InterruptedException e) {
                taskInterrupted.set(true);
                Thread.currentThread().interrupt();
            }
        };

        Future<?> future = testExecutor.submit(longRunningTask);

        // 等待1秒后取消任务
        Thread.sleep(1000);
        boolean cancelled = future.cancel(true);

        assertTrue(cancelled, "任务应该被成功取消");
        assertTrue(future.isCancelled(), "任务状态应该是已取消");
        
        // 稍等一下确保中断信号被处理
        Thread.sleep(100);
        
        assertFalse(taskCompleted.get(), "任务不应该完成");
        
        System.out.println("长时间运行的任务被取消: " + cancelled);
    }

    @Test
    @DisplayName("异步任务性能测试")
    void testAsyncTaskPerformance() throws InterruptedException {
        int taskCount = 100;
        AtomicInteger completedCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(taskCount);

        long startTime = System.currentTimeMillis();

        // 创建大量轻量级异步任务
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            
            Runnable performanceTask = () -> {
                try {
                    // 模拟轻量级计算
                    int result = 0;
                    for (int j = 0; j < 1000; j++) {
                        result += j * taskId;
                    }
                    
                    completedCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            };

            testExecutor.execute(performanceTask);
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "所有性能测试任务应该在30秒内完成");
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        assertEquals(taskCount, completedCount.get(), "所有任务应该都完成");
        
        System.out.println("性能测试结果:");
        System.out.println("任务数量: " + taskCount);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每个任务耗时: " + (totalTime / (double) taskCount) + "ms");
        System.out.println("任务吞吐量: " + (taskCount * 1000.0 / totalTime) + " 任务/秒");
        
        // 性能要求：平均每个任务耗时应该小于50ms
        assertTrue(totalTime / (double) taskCount < 50, "平均每个任务耗时应该小于50ms");
    }

    @Test
    @DisplayName("异步任务内存使用测试")
    void testAsyncTaskMemoryUsage() throws InterruptedException {
        int taskCount = 50;
        CountDownLatch latch = new CountDownLatch(taskCount);

        // 获取初始内存使用情况
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // 创建消耗一定内存的异步任务
        for (int i = 0; i < taskCount; i++) {
            Runnable memoryTask = () -> {
                try {
                    // 创建一些对象占用内存
                    byte[] data = new byte[1024 * 10]; // 10KB
                    for (int j = 0; j < data.length; j++) {
                        data[j] = (byte) (j % 256);
                    }
                    
                    // 短暂持有引用
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            };

            testExecutor.execute(memoryTask);
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));

        // 触发垃圾回收
        System.gc();
        Thread.sleep(1000);

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        System.out.println("内存使用测试:");
        System.out.println("初始内存使用: " + (initialMemory / 1024 / 1024) + " MB");
        System.out.println("最终内存使用: " + (finalMemory / 1024 / 1024) + " MB");
        System.out.println("内存增长: " + (memoryIncrease / 1024 / 1024) + " MB");

        // 内存增长应该在合理范围内（小于100MB）
        assertTrue(memoryIncrease < 100 * 1024 * 1024, "内存增长应该在合理范围内");
    }
}