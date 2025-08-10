package com.dw.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误信息
     */
    private String message;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}