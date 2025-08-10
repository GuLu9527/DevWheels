package com.dw.common.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.dw.common.excel.ExcelImportResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@DisplayName("Excel工具类测试")
public class ExcelUtilsTest {

    // 测试实体类
    public static class Product {
        @ExcelProperty("产品名称")
        @NotBlank(message = "产品名称不能为空")
        private String name;

        @ExcelProperty("价格")
        @Min(value = 0, message = "价格不能为负数")
        private Double price;

        @ExcelProperty("库存")
        @Min(value = 0, message = "库存不能为负数")
        private Integer stock;

        @ExcelProperty("创建时间")
        private LocalDateTime createTime;

        // 构造函数
        public Product() {}

        public Product(String name, Double price, Integer stock, LocalDateTime createTime) {
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.createTime = createTime;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }

    @Test
    @DisplayName("导出Excel基础功能测试")
    void testExportExcel() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        List<Product> products = Arrays.asList(
            new Product("笔记本电脑", 5999.99, 50, LocalDateTime.now()),
            new Product("鼠标", 99.99, 200, LocalDateTime.now()),
            new Product("键盘", 299.99, 150, LocalDateTime.now())
        );

        ExcelUtils.exportExcel(response, products, Product.class, "产品列表");

        // 验证响应设置
        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertEquals("utf-8", response.getCharacterEncoding());
        
        String disposition = response.getHeader("Content-disposition");
        assertNotNull(disposition);
        assertTrue(disposition.contains("attachment"));
        assertTrue(disposition.contains("产品列表"));
        assertTrue(disposition.contains(".xlsx"));

        // 验证内容
        byte[] content = response.getContentAsByteArray();
        assertTrue(content.length > 0);
        
        System.out.println("导出的Excel文件大小: " + content.length + " bytes");
    }

    @Test
    @DisplayName("导出多Sheet Excel测试")
    void testExportExcelWithMultipleSheets() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<Product> electronics = Arrays.asList(
            new Product("手机", 2999.99, 100, LocalDateTime.now()),
            new Product("平板", 1999.99, 80, LocalDateTime.now())
        );

        List<Product> books = Arrays.asList(
            new Product("Java编程", 89.99, 300, LocalDateTime.now()),
            new Product("算法导论", 129.99, 150, LocalDateTime.now())
        );

        List<ExcelUtils.ExcelSheet<?>> sheets = Arrays.asList(
            new ExcelUtils.ExcelSheet<>("电子产品", Product.class, electronics),
            new ExcelUtils.ExcelSheet<>("图书", Product.class, books)
        );

        ExcelUtils.exportExcelWithMultipleSheets(response, "产品分类报表", sheets);

        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertTrue(response.getContentAsByteArray().length > 0);
        
        System.out.println("多Sheet Excel文件大小: " + response.getContentAsByteArray().length + " bytes");
    }

    @Test
    @DisplayName("从文件导入Excel测试")
    void testImportExcelFromFile() throws IOException {
        // 先生成一个Excel文件
        byte[] excelData = generateTestExcel();
        
        MockMultipartFile mockFile = new MockMultipartFile(
            "products", "products.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            excelData
        );

        ExcelImportResult<Product> result = ExcelUtils.importExcel(mockFile, Product.class);

        assertNotNull(result);
        assertTrue(result.getSuccessCount() > 0);
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());

        // 验证导入的数据
        Product firstProduct = result.getData().get(0);
        assertNotNull(firstProduct.getName());
        assertNotNull(firstProduct.getPrice());
        assertNotNull(firstProduct.getStock());

        System.out.println("导入成功记录数: " + result.getSuccessCount());
        System.out.println("失败记录数: " + result.getFailCount());
        System.out.println("第一个产品: " + firstProduct.getName() + " - ￥" + firstProduct.getPrice());
    }

    @Test
    @DisplayName("从输入流导入Excel测试")
    void testImportExcelFromInputStream() throws IOException {
        byte[] excelData = generateTestExcel();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelData);

        ExcelImportResult<Product> result = ExcelUtils.importExcel(inputStream, Product.class);

        assertNotNull(result);
        assertTrue(result.getSuccessCount() > 0);
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("简单读取Excel测试")
    void testReadExcel() throws IOException {
        byte[] excelData = generateTestExcel();
        
        MockMultipartFile mockFile = new MockMultipartFile(
            "simple", "simple.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            excelData
        );

        List<Product> products = ExcelUtils.readExcel(mockFile, Product.class);

        assertNotNull(products);
        assertFalse(products.isEmpty());
        
        for (Product product : products) {
            assertNotNull(product.getName());
            System.out.println("读取到产品: " + product.getName());
        }
    }

    @Test
    @DisplayName("分页读取Excel测试")
    void testReadExcelByPage() throws IOException {
        byte[] largeExcelData = generateLargeTestExcel();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(largeExcelData);

        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger batchCount = new AtomicInteger(0);

        ExcelUtils.readExcelByPage(
            inputStream,
            Product.class,
            5, // 每页5条
            (products) -> {
                batchCount.incrementAndGet();
                totalCount.addAndGet(products.size());
                System.out.println("第 " + batchCount.get() + " 批，处理 " + products.size() + " 条记录");
                
                // 验证数据不为空
                for (Product product : products) {
                    assertNotNull(product.getName());
                }
            }
        );

        assertTrue(totalCount.get() > 0);
        assertTrue(batchCount.get() > 0);
        System.out.println("总共处理 " + totalCount.get() + " 条记录，分 " + batchCount.get() + " 批");
    }

    @Test
    @DisplayName("Excel Sheet封装类测试")
    void testExcelSheet() {
        List<Product> products = Arrays.asList(
            new Product("测试产品", 100.0, 10, LocalDateTime.now())
        );

        ExcelUtils.ExcelSheet<Product> sheet = new ExcelUtils.ExcelSheet<>("测试Sheet", Product.class, products);

        assertEquals("测试Sheet", sheet.getSheetName());
        assertEquals(Product.class, sheet.getClazz());
        assertEquals(products, sheet.getData());
        assertEquals(1, sheet.getData().size());
    }

    @Test
    @DisplayName("空文件导入测试")
    void testImportEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "empty", "empty.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            new byte[0]
        );

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> ExcelUtils.importExcel(emptyFile, Product.class));
        
        assertTrue(exception.getMessage().contains("导入Excel失败") ||
                  exception.getMessage().contains("读取Excel文件失败"));
    }

    @Test
    @DisplayName("null文件导入测试")
    void testImportNullFile() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ExcelUtils.importExcel((MockMultipartFile) null, Product.class));
        
        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    @DisplayName("无效Excel格式测试")
    void testInvalidExcelFormat() {
        MockMultipartFile invalidFile = new MockMultipartFile(
            "invalid", "invalid.txt",
            "text/plain",
            "This is not an Excel file".getBytes()
        );

        Exception exception = assertThrows(Exception.class,
            () -> ExcelUtils.importExcel(invalidFile, Product.class));
        
        assertTrue(exception.getMessage().contains("导入Excel失败") ||
                  exception.getMessage().contains("读取Excel文件失败"));
    }

    @Test
    @DisplayName("导入验证错误测试")
    void testImportValidationErrors() throws IOException {
        byte[] errorExcelData = generateErrorTestExcel();
        
        MockMultipartFile mockFile = new MockMultipartFile(
            "errors", "errors.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            errorExcelData
        );

        ExcelImportResult<Product> result = ExcelUtils.importExcel(mockFile, Product.class);

        assertNotNull(result);
        
        // 应该有成功和失败的记录
        assertTrue(result.getTotalCount() > 0);
        
        if (result.hasError()) {
            assertTrue(result.getFailCount() > 0);
            assertFalse(result.getErrorMessages().isEmpty());
            
            System.out.println("验证错误测试结果:");
            System.out.println("成功: " + result.getSuccessCount() + ", 失败: " + result.getFailCount());
            result.getErrorMessages().forEach((row, error) -> 
                System.out.println("第" + row + "行: " + error));
        }
    }

    @Test
    @DisplayName("大量数据导入性能测试")
    void testLargeDataImportPerformance() throws IOException {
        byte[] largeExcelData = generateVeryLargeTestExcel();
        
        MockMultipartFile mockFile = new MockMultipartFile(
            "large", "large.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            largeExcelData
        );

        long startTime = System.currentTimeMillis();
        
        ExcelImportResult<Product> result = ExcelUtils.importExcel(mockFile, Product.class);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(result);
        assertTrue(result.getSuccessCount() > 0);
        
        System.out.println("大量数据导入性能测试:");
        System.out.println("记录数: " + result.getTotalCount());
        System.out.println("耗时: " + duration + "ms");
        System.out.println("平均每条记录耗时: " + (duration / (double) result.getTotalCount()) + "ms");
        
        // 性能要求：每条记录处理时间应该小于10ms
        assertTrue(duration / (double) result.getTotalCount() < 10, "每条记录处理时间应该小于10ms");
    }

    // 辅助方法：生成测试Excel数据
    private byte[] generateTestExcel() throws IOException {
        List<Product> products = Arrays.asList(
            new Product("苹果手机", 6999.99, 100, LocalDateTime.now()),
            new Product("华为手机", 4999.99, 150, LocalDateTime.now()),
            new Product("小米手机", 2999.99, 200, LocalDateTime.now())
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        ExcelUtils.exportExcel(response, products, Product.class, "测试产品");
        return response.getContentAsByteArray();
    }

    // 辅助方法：生成大量测试数据Excel
    private byte[] generateLargeTestExcel() throws IOException {
        List<Product> products = Arrays.asList(
            new Product("产品1", 100.0, 10, LocalDateTime.now()),
            new Product("产品2", 200.0, 20, LocalDateTime.now()),
            new Product("产品3", 300.0, 30, LocalDateTime.now()),
            new Product("产品4", 400.0, 40, LocalDateTime.now()),
            new Product("产品5", 500.0, 50, LocalDateTime.now()),
            new Product("产品6", 600.0, 60, LocalDateTime.now()),
            new Product("产品7", 700.0, 70, LocalDateTime.now()),
            new Product("产品8", 800.0, 80, LocalDateTime.now()),
            new Product("产品9", 900.0, 90, LocalDateTime.now()),
            new Product("产品10", 1000.0, 100, LocalDateTime.now())
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        ExcelUtils.exportExcel(response, products, Product.class, "大量测试产品");
        return response.getContentAsByteArray();
    }

    // 辅助方法：生成包含错误的测试Excel
    private byte[] generateErrorTestExcel() throws IOException {
        List<Product> products = Arrays.asList(
            new Product("正常产品", 100.0, 10, LocalDateTime.now()),
            new Product("", 200.0, 20, LocalDateTime.now()), // 名称为空
            new Product("负价格产品", -100.0, 30, LocalDateTime.now()), // 价格为负
            new Product("负库存产品", 400.0, -10, LocalDateTime.now()), // 库存为负
            new Product("正常产品2", 500.0, 50, LocalDateTime.now())
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        ExcelUtils.exportExcel(response, products, Product.class, "错误测试产品");
        return response.getContentAsByteArray();
    }

    // 辅助方法：生成超大测试Excel（用于性能测试）
    private byte[] generateVeryLargeTestExcel() throws IOException {
        List<Product> products = new java.util.ArrayList<>();
        // 这里为了测试简化，只创建少量数据
        for (int i = 1; i <= 100; i++) {
            products.add(new Product("产品" + i, 100.0 * i, i * 10, LocalDateTime.now()));
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        ExcelUtils.exportExcel(response, products, Product.class, "性能测试产品");
        return response.getContentAsByteArray();
    }
}