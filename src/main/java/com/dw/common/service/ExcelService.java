package com.dw.common.service;

import com.dw.common.excel.ExcelImportResult;
import com.dw.common.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Excel服务类
 */
@Slf4j
@Service
public class ExcelService {
    
    /**
     * 导出Excel
     * @param response HTTP响应
     * @param data 数据列表
     * @param clazz 实体类
     * @param fileName 文件名
     */
    public <T> void exportExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String fileName) {
        ExcelUtils.exportExcel(response, data, clazz, fileName);
    }
    
    /**
     * 导出多Sheet Excel
     * @param response HTTP响应
     * @param fileName 文件名
     * @param sheets Sheet数据
     */
    public void exportMultipleSheets(HttpServletResponse response, String fileName, List<ExcelUtils.ExcelSheet<?>> sheets) {
        ExcelUtils.exportExcelWithMultipleSheets(response, fileName, sheets);
    }
    
    /**
     * 导入Excel
     * @param file 上传文件
     * @param clazz 实体类
     * @return 导入结果
     */
    public <T> ExcelImportResult<T> importExcel(MultipartFile file, Class<T> clazz) {
        return ExcelUtils.importExcel(file, clazz);
    }
    
    /**
     * 导入Excel并处理数据
     * @param file 上传文件
     * @param clazz 实体类
     * @param processor 数据处理器
     * @return 导入结果
     */
    public <T> ExcelImportResult<T> importExcelAndProcess(MultipartFile file, Class<T> clazz, Function<List<T>, Boolean> processor) {
        ExcelImportResult<T> result = importExcel(file, clazz);
        
        if (!result.hasError() && !result.getData().isEmpty()) {
            try {
                Boolean success = processor.apply(result.getData());
                if (!success) {
                    log.error("Excel数据处理失败");
                    throw new RuntimeException("数据处理失败");
                }
            } catch (Exception e) {
                log.error("Excel数据处理异常", e);
                throw new RuntimeException("数据处理异常: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 大文件分页导入Excel
     * @param file 上传文件
     * @param clazz 实体类
     * @param pageSize 每页大小
     * @param consumer 数据消费者
     */
    public <T> void importLargeExcel(MultipartFile file, Class<T> clazz, int pageSize, Consumer<List<T>> consumer) {
        try {
            ExcelUtils.readExcelByPage(file.getInputStream(), clazz, pageSize, consumer);
        } catch (Exception e) {
            log.error("大文件导入失败", e);
            throw new RuntimeException("大文件导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成Excel模板
     * @param response HTTP响应
     * @param clazz 实体类
     * @param fileName 文件名
     */
    public <T> void generateTemplate(HttpServletResponse response, Class<T> clazz, String fileName) {
        // 生成空的模板文件
        ExcelUtils.exportExcel(response, List.of(), clazz, fileName + "_模板");
    }
}