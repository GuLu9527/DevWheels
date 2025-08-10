package com.dw.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 异步任务服务
 */
@Slf4j
@Service
public class AsyncTaskService {
    
    /**
     * 无返回值的异步任务
     * @param task 任务
     */
    @Async("taskExecutor")
    public void executeAsync(Runnable task) {
        try {
            log.info("开始执行异步任务: {}", Thread.currentThread().getName());
            long startTime = System.currentTimeMillis();
            
            task.run();
            
            long endTime = System.currentTimeMillis();
            log.info("异步任务执行完成，耗时: {}ms", endTime - startTime);
            
        } catch (Exception e) {
            log.error("异步任务执行异常", e);
        }
    }
    
    /**
     * 有返回值的异步任务
     * @param task 任务
     * @return Future结果
     */
    @Async("taskExecutor")
    public <T> Future<T> executeAsyncWithResult(Supplier<T> task) {
        try {
            log.info("开始执行异步任务: {}", Thread.currentThread().getName());
            long startTime = System.currentTimeMillis();
            
            T result = task.get();
            
            long endTime = System.currentTimeMillis();
            log.info("异步任务执行完成，耗时: {}ms", endTime - startTime);
            
            return AsyncResult.forValue(result);
            
        } catch (Exception e) {
            log.error("异步任务执行异常", e);
            return AsyncResult.forExecutionException(e);
        }
    }
    
    /**
     * 异步发送邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    @Async("mailExecutor")
    public void sendMailAsync(String to, String subject, String content) {
        try {
            log.info("开始异步发送邮件: {} -> {}", subject, to);
            
            // 这里集成具体的邮件发送逻辑
            // mailService.send(to, subject, content);
            
            // 模拟邮件发送
            Thread.sleep(2000);
            
            log.info("邮件发送成功");
            
        } catch (Exception e) {
            log.error("邮件发送失败", e);
        }
    }
    
    /**
     * 异步处理文件
     * @param filePath 文件路径
     * @param processor 文件处理器
     */
    @Async("fileExecutor")
    public void processFileAsync(String filePath, Consumer<String> processor) {
        try {
            log.info("开始异步处理文件: {}", filePath);
            long startTime = System.currentTimeMillis();
            
            processor.accept(filePath);
            
            long endTime = System.currentTimeMillis();
            log.info("文件处理完成，耗时: {}ms", endTime - startTime);
            
        } catch (Exception e) {
            log.error("文件处理失败", e);
        }
    }
    
    /**
     * 异步数据同步
     * @param dataProcessor 数据处理器
     */
    @Async("taskExecutor")
    public void syncDataAsync(Runnable dataProcessor) {
        try {
            log.info("开始异步数据同步: {}", Thread.currentThread().getName());
            long startTime = System.currentTimeMillis();
            
            dataProcessor.run();
            
            long endTime = System.currentTimeMillis();
            log.info("数据同步完成，耗时: {}ms", endTime - startTime);
            
        } catch (Exception e) {
            log.error("数据同步异常", e);
        }
    }
    
    /**
     * 延迟执行异步任务
     * @param task 任务
     * @param delayMs 延迟毫秒数
     */
    @Async("taskExecutor")
    public void executeAsyncWithDelay(Runnable task, long delayMs) {
        try {
            if (delayMs > 0) {
                Thread.sleep(delayMs);
            }
            
            log.info("开始执行延迟异步任务: {}", Thread.currentThread().getName());
            long startTime = System.currentTimeMillis();
            
            task.run();
            
            long endTime = System.currentTimeMillis();
            log.info("延迟异步任务执行完成，耗时: {}ms", endTime - startTime);
            
        } catch (InterruptedException e) {
            log.warn("延迟异步任务被中断");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("延迟异步任务执行异常", e);
        }
    }
}