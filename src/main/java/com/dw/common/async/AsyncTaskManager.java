package com.dw.common.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 异步任务管理器
 */
@Slf4j
@Component
public class AsyncTaskManager {
    
    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;
    
    /**
     * 执行异步任务（带回调）
     * @param task 任务
     * @param callback 回调
     */
    public <T> void executeWithCallback(Supplier<T> task, AsyncTaskCallback<T> callback) {
        CompletableFuture.supplyAsync(task, taskExecutor)
                .whenComplete((result, exception) -> {
                    try {
                        if (exception != null) {
                            log.error("异步任务执行失败", exception);
                            callback.onFailure(exception instanceof Exception ? 
                                (Exception) exception : new RuntimeException(exception));
                        } else {
                            callback.onSuccess(result);
                        }
                    } finally {
                        callback.onComplete();
                    }
                });
    }
    
    /**
     * 执行异步任务（无返回值，带回调）
     * @param task 任务
     * @param callback 回调
     */
    public void executeWithCallback(Runnable task, AsyncTaskCallback<Void> callback) {
        CompletableFuture.runAsync(task, taskExecutor)
                .whenComplete((result, exception) -> {
                    try {
                        if (exception != null) {
                            log.error("异步任务执行失败", exception);
                            callback.onFailure(exception instanceof Exception ? 
                                (Exception) exception : new RuntimeException(exception));
                        } else {
                            callback.onSuccess(null);
                        }
                    } finally {
                        callback.onComplete();
                    }
                });
    }
    
    /**
     * 执行带超时的异步任务
     * @param task 任务
     * @param timeoutSeconds 超时秒数
     * @param callback 回调
     */
    public <T> void executeWithTimeout(Supplier<T> task, long timeoutSeconds, AsyncTaskCallback<T> callback) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(task, taskExecutor);
        
        future.orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .whenComplete((result, exception) -> {
                    try {
                        if (exception != null) {
                            log.error("异步任务执行失败", exception);
                            callback.onFailure(exception instanceof Exception ? 
                                (Exception) exception : new RuntimeException(exception));
                        } else {
                            callback.onSuccess(result);
                        }
                    } finally {
                        callback.onComplete();
                    }
                });
    }
    
    /**
     * 链式执行异步任务
     * @param firstTask 第一个任务
     * @param secondTask 第二个任务
     * @param callback 回调
     */
    public <T, R> void executeChain(Supplier<T> firstTask, 
                                   java.util.function.Function<T, R> secondTask, 
                                   AsyncTaskCallback<R> callback) {
        CompletableFuture.supplyAsync(firstTask, taskExecutor)
                .thenApplyAsync(secondTask, taskExecutor)
                .whenComplete((result, exception) -> {
                    try {
                        if (exception != null) {
                            log.error("链式异步任务执行失败", exception);
                            callback.onFailure(exception instanceof Exception ? 
                                (Exception) exception : new RuntimeException(exception));
                        } else {
                            callback.onSuccess(result);
                        }
                    } finally {
                        callback.onComplete();
                    }
                });
    }
    
    /**
     * 并行执行多个异步任务
     * @param tasks 任务列表
     * @param callback 回调
     */
    @SafeVarargs
    public final <T> void executeParallel(AsyncTaskCallback<T[]> callback, Supplier<T>... tasks) {
        CompletableFuture<T>[] futures = new CompletableFuture[tasks.length];
        
        for (int i = 0; i < tasks.length; i++) {
            futures[i] = CompletableFuture.supplyAsync(tasks[i], taskExecutor);
        }
        
        CompletableFuture.allOf(futures)
                .whenComplete((result, exception) -> {
                    try {
                        if (exception != null) {
                            log.error("并行异步任务执行失败", exception);
                            callback.onFailure(exception instanceof Exception ? 
                                (Exception) exception : new RuntimeException(exception));
                        } else {
                            try {
                                @SuppressWarnings("unchecked")
                                T[] results = (T[]) new Object[futures.length];
                                for (int i = 0; i < futures.length; i++) {
                                    results[i] = futures[i].join();
                                }
                                callback.onSuccess(results);
                            } catch (Exception e) {
                                log.error("获取并行任务结果失败", e);
                                callback.onFailure(e);
                            }
                        }
                    } finally {
                        callback.onComplete();
                    }
                });
    }
}