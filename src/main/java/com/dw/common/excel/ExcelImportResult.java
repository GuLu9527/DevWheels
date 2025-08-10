package com.dw.common.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入结果
 */
@Data
public class ExcelImportResult<T> {
    
    /**
     * 导入成功的数据
     */
    private List<T> data = new ArrayList<>();
    
    /**
     * 成功条数
     */
    private int successCount = 0;
    
    /**
     * 失败条数
     */
    private int failCount = 0;
    
    /**
     * 错误信息 (行号 -> 错误信息)
     */
    private Map<Integer, String> errorMessages = new HashMap<>();
    
    /**
     * 添加成功数据
     */
    public void addData(List<T> dataList) {
        this.data.addAll(dataList);
    }
    
    /**
     * 添加单条数据
     */
    public void addData(T item) {
        this.data.add(item);
    }
    
    /**
     * 增加成功计数
     */
    public void incrementSuccessCount() {
        this.successCount++;
    }
    
    /**
     * 增加失败计数
     */
    public void incrementFailCount() {
        this.failCount++;
    }
    
    /**
     * 添加错误信息
     */
    public void addError(int rowNum, String errorMsg) {
        this.errorMessages.put(rowNum, errorMsg);
    }
    
    /**
     * 是否有错误
     */
    public boolean hasError() {
        return failCount > 0;
    }
    
    /**
     * 获取总条数
     */
    public int getTotalCount() {
        return successCount + failCount;
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        if (getTotalCount() == 0) {
            return 0.0;
        }
        return (double) successCount / getTotalCount() * 100;
    }
    
    /**
     * 获取错误摘要
     */
    public String getErrorSummary() {
        if (!hasError()) {
            return "导入成功";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("导入完成，总计: ").append(getTotalCount()).append("条，")
               .append("成功: ").append(successCount).append("条，")
               .append("失败: ").append(failCount).append("条。");
        
        if (errorMessages.size() <= 5) {
            // 错误较少时，显示具体错误
            summary.append("错误详情：");
            errorMessages.forEach((rowNum, errorMsg) -> 
                summary.append("\n第").append(rowNum).append("行: ").append(errorMsg)
            );
        } else {
            // 错误较多时，只显示前5条
            summary.append("部分错误详情（前5条）：");
            errorMessages.entrySet().stream()
                .limit(5)
                .forEach(entry -> 
                    summary.append("\n第").append(entry.getKey()).append("行: ").append(entry.getValue())
                );
            summary.append("\n...还有").append(errorMessages.size() - 5).append("条错误");
        }
        
        return summary.toString();
    }
}