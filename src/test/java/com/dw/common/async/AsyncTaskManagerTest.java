package com.dw.common.async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
@DisplayName("异步任务管理器测试")
public class AsyncTaskManagerTest {

    @Mock
    private Executor taskExecutor;

    @InjectMocks
    private AsyncTaskManager asyncTaskManager;

    private ExecutorService testExecutor;

    @BeforeEach
    void setUp() {
        testExecutor = Executors.newFixedThreadPool(5);
        
        // 配置Mock，让taskExecutor实际执行任务
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            testExecutor.execute(task);
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("执行带回调的有返回值任务测试")
    void testExecuteWithCallbackWithResult() throws InterruptedException {
        AtomicReference<String> callbackResult = new AtomicReference<>();
        AtomicBoolean callbackSuccess = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建任务
        Supplier<String> task = () -> {
            try {
                Thread.sleep(100);
                return "任务执行成功";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        // 创建回调
        AsyncTaskCallback<String> callback = new AsyncTaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callbackResult.set(result);
                callbackSuccess.set(true);
            }

            @Override
            public void onFailure(Exception exception) {
                // 不应该被调用
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行任务
        asyncTaskManager.executeWithCallback(task, callback);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackSuccess.get(), "回调应该成功执行");
        assertTrue(callbackComplete.get(), "回调应该完成");
        assertEquals("任务执行成功", callbackResult.get());

        System.out.println("回调结果: " + callbackResult.get());
    }

    @Test
    @DisplayName("执行带回调的无返回值任务测试")
    void testExecuteWithCallbackNoResult() throws InterruptedException {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        AtomicBoolean callbackSuccess = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建任务
        Runnable task = () -> {
            try {
                Thread.sleep(100);
                taskExecuted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // 创建回调
        AsyncTaskCallback<Void> callback = new AsyncTaskCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callbackSuccess.set(true);
            }

            @Override
            public void onFailure(Exception exception) {
                // 不应该被调用
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行任务
        asyncTaskManager.executeWithCallback(task, callback);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(taskExecuted.get(), "任务应该被执行");
        assertTrue(callbackSuccess.get(), "回调应该成功执行");
        assertTrue(callbackComplete.get(), "回调应该完成");

        System.out.println("无返回值任务执行完成");
    }

    @Test
    @DisplayName("任务执行失败回调测试")
    void testExecuteWithCallbackFailure() throws InterruptedException {
        AtomicReference<Exception> callbackException = new AtomicReference<>();
        AtomicBoolean callbackFailure = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建会失败的任务
        Supplier<String> failingTask = () -> {
            throw new RuntimeException("任务执行失败");
        };

        // 创建回调
        AsyncTaskCallback<String> callback = new AsyncTaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // 不应该被调用
            }

            @Override
            public void onFailure(Exception exception) {
                callbackException.set(exception);
                callbackFailure.set(true);
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行任务
        asyncTaskManager.executeWithCallback(failingTask, callback);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackFailure.get(), "应该调用失败回调");
        assertTrue(callbackComplete.get(), "应该调用完成回调");
        assertNotNull(callbackException.get(), "应该捕获到异常");
        assertEquals("任务执行失败", callbackException.get().getMessage());

        System.out.println("捕获到异常: " + callbackException.get().getMessage());
    }

    @Test
    @DisplayName("带超时的任务执行测试")
    void testExecuteWithTimeout() throws InterruptedException {
        AtomicReference<String> callbackResult = new AtomicReference<>();
        AtomicBoolean callbackSuccess = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建快速完成的任务
        Supplier<String> quickTask = () -> {
            try {
                Thread.sleep(100); // 0.1秒，小于超时时间
                return "快速任务完成";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        // 创建回调
        AsyncTaskCallback<String> callback = new AsyncTaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callbackResult.set(result);
                callbackSuccess.set(true);
            }

            @Override
            public void onFailure(Exception exception) {
                System.err.println("任务失败: " + exception.getMessage());
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行任务（超时时间2秒）
        asyncTaskManager.executeWithTimeout(quickTask, 2, callback);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackSuccess.get(), "快速任务应该成功完成");
        assertTrue(callbackComplete.get(), "回调应该完成");
        assertEquals("快速任务完成", callbackResult.get());

        System.out.println("超时测试结果: " + callbackResult.get());
    }

    @Test
    @DisplayName("任务超时失败测试")
    void testExecuteWithTimeoutFailure() throws InterruptedException {
        AtomicReference<Exception> callbackException = new AtomicReference<>();
        AtomicBoolean callbackFailure = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建超时任务
        Supplier<String> timeoutTask = () -> {
            try {
                Thread.sleep(3000); // 3秒，大于超时时间
                return "不应该完成";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("任务被中断");
            }
        };

        // 创建回调
        AsyncTaskCallback<String> callback = new AsyncTaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // 不应该被调用
            }

            @Override
            public void onFailure(Exception exception) {
                callbackException.set(exception);
                callbackFailure.set(true);
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行任务（超时时间1秒）
        asyncTaskManager.executeWithTimeout(timeoutTask, 1, callback);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackFailure.get(), "超时任务应该失败");
        assertTrue(callbackComplete.get(), "回调应该完成");
        assertNotNull(callbackException.get(), "应该捕获到超时异常");

        System.out.println("超时异常: " + callbackException.get().getClass().getSimpleName());
    }

    @Test
    @DisplayName("链式任务执行测试")
    void testExecuteChain() throws InterruptedException {
        AtomicReference<String> chainResult = new AtomicReference<>();
        AtomicBoolean callbackSuccess = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 第一个任务
        Supplier<Integer> firstTask = () -> {
            try {
                Thread.sleep(100);
                return 42;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        // 第二个任务
        Function<Integer, String> secondTask = (input) -> {
            try {
                Thread.sleep(100);
                return "处理结果: " + (input * 2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        // 创建回调
        AsyncTaskCallback<String> callback = new AsyncTaskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                chainResult.set(result);
                callbackSuccess.set(true);
            }

            @Override
            public void onFailure(Exception exception) {
                System.err.println("链式任务失败: " + exception.getMessage());
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行链式任务
        asyncTaskManager.executeChain(firstTask, secondTask, callback);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackSuccess.get(), "链式任务应该成功完成");
        assertTrue(callbackComplete.get(), "回调应该完成");
        assertEquals("处理结果: 84", chainResult.get());

        System.out.println("链式任务结果: " + chainResult.get());
    }

    @Test
    @DisplayName("并行任务执行测试")
    void testExecuteParallel() throws InterruptedException {
        AtomicReference<String[]> parallelResults = new AtomicReference<>();
        AtomicBoolean callbackSuccess = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建多个并行任务
        Supplier<String> task1 = () -> {
            try {
                Thread.sleep(100);
                return "任务1完成";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        Supplier<String> task2 = () -> {
            try {
                Thread.sleep(150);
                return "任务2完成";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        Supplier<String> task3 = () -> {
            try {
                Thread.sleep(80);
                return "任务3完成";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        // 创建回调
        AsyncTaskCallback<String[]> callback = new AsyncTaskCallback<String[]>() {
            @Override
            public void onSuccess(String[] results) {
                parallelResults.set(results);
                callbackSuccess.set(true);
            }

            @Override
            public void onFailure(Exception exception) {
                System.err.println("并行任务失败: " + exception.getMessage());
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行并行任务
        asyncTaskManager.executeParallel(callback, task1, task2, task3);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackSuccess.get(), "并行任务应该成功完成");
        assertTrue(callbackComplete.get(), "回调应该完成");
        assertNotNull(parallelResults.get(), "应该有并行结果");
        assertEquals(3, parallelResults.get().length, "应该有3个结果");

        String[] results = parallelResults.get();
        assertEquals("任务1完成", results[0]);
        assertEquals("任务2完成", results[1]);
        assertEquals("任务3完成", results[2]);

        System.out.println("并行任务结果: " + String.join(", ", results));
    }

    @Test
    @DisplayName("并行任务部分失败测试")
    void testExecuteParallelWithFailure() throws InterruptedException {
        AtomicReference<Exception> parallelException = new AtomicReference<>();
        AtomicBoolean callbackFailure = new AtomicBoolean(false);
        AtomicBoolean callbackComplete = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // 创建任务，其中一个会失败
        Supplier<String> successTask = () -> {
            try {
                Thread.sleep(100);
                return "成功任务";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };

        Supplier<String> failureTask = () -> {
            throw new RuntimeException("任务失败");
        };

        // 创建回调
        AsyncTaskCallback<String[]> callback = new AsyncTaskCallback<String[]>() {
            @Override
            public void onSuccess(String[] results) {
                // 不应该被调用
            }

            @Override
            public void onFailure(Exception exception) {
                parallelException.set(exception);
                callbackFailure.set(true);
            }

            @Override
            public void onComplete() {
                callbackComplete.set(true);
                latch.countDown();
            }
        };

        // 执行并行任务
        asyncTaskManager.executeParallel(callback, successTask, failureTask);

        // 等待完成
        latch.await(5, TimeUnit.SECONDS);

        assertTrue(callbackFailure.get(), "并行任务应该失败");
        assertTrue(callbackComplete.get(), "回调应该完成");
        assertNotNull(parallelException.get(), "应该捕获到异常");

        System.out.println("并行任务异常: " + parallelException.get().getMessage());
    }

    @Test
    @DisplayName("任务执行性能测试")
    void testTaskExecutionPerformance() throws InterruptedException {
        int taskCount = 50;
        AtomicInteger completedTasks = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(taskCount);

        long startTime = System.currentTimeMillis();

        // 创建多个轻量级任务
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;

            Supplier<Integer> task = () -> {
                // 轻量级计算
                int result = 0;
                for (int j = 0; j < 1000; j++) {
                    result += j * taskId;
                }
                return result;
            };

            AsyncTaskCallback<Integer> callback = new AsyncTaskCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    completedTasks.incrementAndGet();
                }

                @Override
                public void onFailure(Exception exception) {
                    // 记录失败
                }

                @Override
                public void onComplete() {
                    latch.countDown();
                }
            };

            asyncTaskManager.executeWithCallback(task, callback);
        }

        // 等待所有任务完成
        latch.await(30, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        assertEquals(taskCount, completedTasks.get(), "所有任务应该都完成");

        System.out.println("任务管理器性能测试:");
        System.out.println("任务数量: " + taskCount);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均每个任务耗时: " + (totalTime / (double) taskCount) + "ms");
        System.out.println("任务吞吐量: " + (taskCount * 1000.0 / totalTime) + " 任务/秒");

        // 性能要求：平均每个任务耗时应该小于100ms
        assertTrue(totalTime / (double) taskCount < 100, "平均每个任务耗时应该小于100ms");
    }
}