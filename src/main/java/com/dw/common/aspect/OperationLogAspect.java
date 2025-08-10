package com.dw.common.aspect;

import com.dw.common.annotation.OperationLog;
import com.dw.common.entity.SysOperationLog;
import com.dw.common.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    
    @Around("@annotation(com.dw.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();
        
        // 构建日志对象
        SysOperationLog logEntity = new SysOperationLog();
        logEntity.setOperation(operationLog.value());
        logEntity.setOperationType(operationLog.type().getDescription());
        logEntity.setMethod(request != null ? request.getMethod() : "");
        logEntity.setUri(request != null ? request.getRequestURI() : "");
        logEntity.setIp(getClientIp(request));
        
        // 记录请求参数
        if (operationLog.recordParams()) {
            logEntity.setParams(JsonUtils.toJson(joinPoint.getArgs()));
        }
        
        Object result = null;
        try {
            // 执行方法
            result = joinPoint.proceed();
            logEntity.setSuccess(true);
            
            // 记录返回结果
            if (operationLog.recordResult()) {
                logEntity.setResult(JsonUtils.toJson(result));
            }
            
        } catch (Exception e) {
            logEntity.setSuccess(false);
            logEntity.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            logEntity.setExecutionTime(executionTime);
            
            // 异步保存日志（这里先打印，实际项目中应该保存到数据库）
            saveOperationLog(logEntity);
        }
        
        return result;
    }
    
    /**
     * 获取HTTP请求对象
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多级代理的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * 保存操作日志
     */
    private void saveOperationLog(SysOperationLog logEntity) {
        // 这里先打印日志，实际项目中应该异步保存到数据库
        log.info("操作日志: {}", JsonUtils.toJson(logEntity));
        
        // TODO: 异步保存到数据库
        // operationLogService.save(logEntity);
    }
}