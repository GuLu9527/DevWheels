package com.dw.common.exception;

import com.dw.common.result.DWResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常处理（@RequestBody 校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DWResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        log.warn("参数校验失败: {}", message.toString());
        return DWResult.fail(400, "参数校验失败: " + message.toString());
    }

    /**
     * 参数校验异常处理（@ModelAttribute 校验）
     */
    @ExceptionHandler(BindException.class)
    public DWResult<?> handleBindException(BindException e) {
        StringBuilder message = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        log.warn("参数绑定校验失败: {}", message.toString());
        return DWResult.fail(400, "参数校验失败: " + message.toString());
    }

    /**
     * 参数校验异常处理（@RequestParam 和 @PathVariable 校验）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public DWResult<?> handleConstraintViolationException(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
        }
        log.warn("约束校验失败: {}", message.toString());
        return DWResult.fail(400, "参数校验失败: " + message.toString());
    }

    /**
     * 自定义业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public DWResult<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return DWResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 空指针异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    public DWResult<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常: ", e);
        return DWResult.fail(500, "系统内部错误");
    }

    /**
     * 非法参数异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public DWResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return DWResult.fail(400, "参数错误: " + e.getMessage());
    }

    /**
     * 其他未知异常处理
     */
    @ExceptionHandler(Exception.class)
    public DWResult<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return DWResult.fail(500, "系统内部错误，请联系管理员");
    }
}