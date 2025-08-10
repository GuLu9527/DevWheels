package com.dw.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.dw.common.excel.ExcelImportListener;
import com.dw.common.excel.ExcelImportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtils {
    
    /**
     * 导出Excel到响应流
     * @param response HTTP响应
     * @param data 数据列表
     * @param clazz 实体类
     * @param fileName 文件名
     */
    public static <T> void exportExcel(HttpServletResponse response, List<T> data, Class<T> clazz, String fileName) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet("数据")
                    .doWrite(data);
                    
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出多个Sheet的Excel
     * @param response HTTP响应
     * @param fileName 文件名
     * @param sheets Sheet数据
     */
    public static void exportExcelWithMultipleSheets(HttpServletResponse response, String fileName, List<ExcelSheet<?>> sheets) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            
            for (int i = 0; i < sheets.size(); i++) {
                ExcelSheet<?> sheetData = sheets.get(i);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetData.getSheetName())
                        .head(sheetData.getClazz())
                        .build();
                excelWriter.write(sheetData.getData(), writeSheet);
            }
            
            excelWriter.finish();
            
        } catch (IOException e) {
            log.error("导出多Sheet Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }
    
    /**
     * 从文件导入Excel
     * @param file 上传文件
     * @param clazz 实体类
     * @return 导入结果
     */
    public static <T> ExcelImportResult<T> importExcel(MultipartFile file, Class<T> clazz) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        try {
            return importExcel(file.getInputStream(), clazz);
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("读取Excel文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 从输入流导入Excel
     * @param inputStream 输入流
     * @param clazz 实体类
     * @return 导入结果
     */
    public static <T> ExcelImportResult<T> importExcel(InputStream inputStream, Class<T> clazz) {
        ExcelImportListener<T> listener = new ExcelImportListener<>();
        
        try {
            EasyExcel.read(inputStream, clazz, listener)
                    .sheet()
                    .doRead();
                    
            return listener.getResult();
            
        } catch (Exception e) {
            log.error("导入Excel失败", e);
            throw new RuntimeException("导入Excel失败: " + e.getMessage());
        }
    }
    
    /**
     * 简单读取Excel（适用于小文件）
     * @param file 上传文件
     * @param clazz 实体类
     * @return 数据列表
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        try {
            return EasyExcel.read(file.getInputStream())
                    .head(clazz)
                    .sheet()
                    .doReadSync();
                    
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("读取Excel文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页读取Excel（适用于大文件）
     * @param inputStream 输入流
     * @param clazz 实体类
     * @param pageSize 每页大小
     * @param consumer 数据消费者
     */
    public static <T> void readExcelByPage(InputStream inputStream, Class<T> clazz, int pageSize, java.util.function.Consumer<List<T>> consumer) {
        EasyExcel.read(inputStream, clazz, new PageReadListener<T>(consumer, pageSize))
                .sheet()
                .doRead();
    }
    
    /**
     * Excel Sheet数据封装
     */
    public static class ExcelSheet<T> {
        private String sheetName;
        private Class<T> clazz;
        private List<T> data;
        
        public ExcelSheet(String sheetName, Class<T> clazz, List<T> data) {
            this.sheetName = sheetName;
            this.clazz = clazz;
            this.data = data;
        }
        
        // Getters
        public String getSheetName() {
            return sheetName;
        }
        
        public Class<T> getClazz() {
            return clazz;
        }
        
        public List<T> getData() {
            return data;
        }
    }
}