package com.dw.common.async;

/**
 * 异步任务回调接口
 */
public interface AsyncTaskCallback<T> {
    
    /**
     * 任务执行成功回调
     * @param result 执行结果
     */
    void onSuccess(T result);
    
    /**
     * 任务执行失败回调
     * @param exception 异常信息
     */
    void onFailure(Exception exception);
    
    /**
     * 任务执行完成回调（无论成功失败都会调用）
     */
    default void onComplete() {
        // 默认空实现
    }
}