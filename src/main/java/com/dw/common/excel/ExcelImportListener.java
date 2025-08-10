package com.dw.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * Excel导入监听器
 */
@Slf4j
public class ExcelImportListener<T> implements ReadListener<T> {
    
    /**
     * 每隔100条处理一次数据，然后清空list，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    
    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    
    /**
     * 导入结果
     */
    private final ExcelImportResult<T> result = new ExcelImportResult<>();
    
    /**
     * 数据校验器
     */
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Override
    public void invoke(T data, AnalysisContext context) {
        // 校验数据
        Set<ConstraintViolation<T>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                errorMsg.append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("; ");
            }
            
            result.addError(context.readRowHolder().getRowIndex() + 1, errorMsg.toString());
            result.incrementFailCount();
            return;
        }
        
        cachedDataList.add(data);
        result.incrementSuccessCount();
        
        // 达到BATCH_COUNT了，需要去处理一次数据，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成，成功: {}条, 失败: {}条", result.getSuccessCount(), result.getFailCount());
    }
    
    /**
     * 保存数据到结果集
     */
    private void saveData() {
        if (!cachedDataList.isEmpty()) {
            result.addData(cachedDataList);
            log.info("处理{}条数据", cachedDataList.size());
        }
    }
    
    /**
     * 获取导入结果
     */
    public ExcelImportResult<T> getResult() {
        return result;
    }
}