package com.dw.common.service;

import com.alibaba.excel.annotation.ExcelProperty;
import com.dw.common.excel.ExcelImportResult;
import com.dw.common.utils.ExcelUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@DisplayName("Excel服务测试")
public class ExcelServiceTest {

    private ExcelService excelService;

    // 测试用的实体类
    public static class TestUser {
        @ExcelProperty("姓名")
        @NotBlank(message = "姓名不能为空")
        private String name;

        @ExcelProperty("年龄")
        @NotNull(message = "年龄不能为空")
        @Min(value = 1, message = "年龄必须大于0")
        private Integer age;

        @ExcelProperty("邮箱")
        private String email;

        @ExcelProperty("创建时间")
        private LocalDateTime createTime;

        // 构造函数
        public TestUser() {}

        public TestUser(String name, Integer age, String email, LocalDateTime createTime) {
            this.name = name;
            this.age = age;
            this.email = email;
            this.createTime = createTime;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }

    @BeforeEach
    void setUp() {
        excelService = new ExcelService();
    }

    @Test
    @DisplayName("导出Excel测试")
    void testExportExcel() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        List<TestUser> testData = Arrays.asList(
            new TestUser("张三", 25, "zhangsan@test.com", LocalDateTime.now()),
            new TestUser("李四", 30, "lisi@test.com", LocalDateTime.now()),
            new TestUser("王五", 28, "wangwu@test.com", LocalDateTime.now())
        );

        excelService.exportExcel(response, testData, TestUser.class, "用户列表");

        // 验证响应头
        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertNotNull(response.getHeader("Content-disposition"));
        assertTrue(response.getHeader("Content-disposition").contains("用户列表"));

        // 验证响应内容不为空
        assertTrue(response.getContentAsByteArray().length > 0);
        
        System.out.println("导出Excel文件大小: " + response.getContentAsByteArray().length + " bytes");
    }

    @Test
    @DisplayName("导出多Sheet Excel测试")
    void testExportMultipleSheets() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        List<TestUser> users1 = Arrays.asList(
            new TestUser("用户1", 25, "user1@test.com", LocalDateTime.now()),
            new TestUser("用户2", 30, "user2@test.com", LocalDateTime.now())
        );

        List<TestUser> users2 = Arrays.asList(
            new TestUser("用户3", 35, "user3@test.com", LocalDateTime.now()),
            new TestUser("用户4", 40, "user4@test.com", LocalDateTime.now())
        );

        List<ExcelUtils.ExcelSheet<?>> sheets = Arrays.asList(
            new ExcelUtils.ExcelSheet<>("活跃用户", TestUser.class, users1),
            new ExcelUtils.ExcelSheet<>("普通用户", TestUser.class, users2)
        );

        excelService.exportMultipleSheets(response, "多Sheet用户报表", sheets);

        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertTrue(response.getContentAsByteArray().length > 0);
        
        System.out.println("多Sheet Excel文件大小: " + response.getContentAsByteArray().length + " bytes");
    }

    @Test
    @DisplayName("导入Excel成功测试")
    void testImportExcelSuccess() throws IOException {
        // 创建测试Excel数据
        byte[] excelData = createTestExcelData();
        MockMultipartFile mockFile = new MockMultipartFile(
            "excel", "test.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            excelData
        );

        ExcelImportResult<TestUser> result = excelService.importExcel(mockFile, TestUser.class);

        assertNotNull(result);
        assertTrue(result.getSuccessCount() > 0);
        assertEquals(0, result.getFailCount());
        assertFalse(result.hasError());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());

        System.out.println("导入成功记录数: " + result.getSuccessCount());
        System.out.println("导入的第一条记录: " + result.getData().get(0).getName());
    }

    @Test
    @DisplayName("导入空文件测试")
    void testImportEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "empty", "empty.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            new byte[0]
        );

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> excelService.importExcel(emptyFile, TestUser.class));
        
        assertTrue(exception.getMessage().contains("读取Excel文件失败") || 
                  exception.getMessage().contains("导入Excel失败"));
    }

    @Test
    @DisplayName("导入null文件测试")
    void testImportNullFile() {
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> excelService.importExcel(null, TestUser.class));
        
        assertTrue(exception.getMessage().contains("读取Excel文件失败") || 
                  exception.getMessage().contains("导入Excel失败"));
    }

    @Test
    @DisplayName("导入Excel并处理数据测试")
    void testImportExcelAndProcess() throws IOException {
        byte[] excelData = createTestExcelData();
        MockMultipartFile mockFile = new MockMultipartFile(
            "excel", "test.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            excelData
        );

        AtomicBoolean processorCalled = new AtomicBoolean(false);
        AtomicInteger processedCount = new AtomicInteger(0);

        ExcelImportResult<TestUser> result = excelService.importExcelAndProcess(
            mockFile, 
            TestUser.class, 
            (data) -> {
                processorCalled.set(true);
                processedCount.set(data.size());
                // 模拟处理成功
                return true;
            }
        );

        assertNotNull(result);
        assertTrue(processorCalled.get());
        assertTrue(processedCount.get() > 0);
        assertEquals(result.getData().size(), processedCount.get());
    }

    @Test
    @DisplayName("导入Excel处理失败测试")
    void testImportExcelProcessFailure() throws IOException {
        byte[] excelData = createTestExcelData();
        MockMultipartFile mockFile = new MockMultipartFile(
            "excel", "test.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            excelData
        );

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> excelService.importExcelAndProcess(
                mockFile, 
                TestUser.class, 
                (data) -> false // 处理失败
            ));

        assertTrue(exception.getMessage().contains("数据处理失败"));
    }

    @Test
    @DisplayName("导入Excel处理异常测试")
    void testImportExcelProcessException() throws IOException {
        byte[] excelData = createTestExcelData();
        MockMultipartFile mockFile = new MockMultipartFile(
            "excel", "test.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            excelData
        );

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> excelService.importExcelAndProcess(
                mockFile, 
                TestUser.class, 
                (data) -> {
                    throw new RuntimeException("处理过程中出现异常");
                }
            ));

        assertTrue(exception.getMessage().contains("数据处理异常"));
    }

    @Test
    @DisplayName("大文件分页导入测试")
    void testImportLargeExcel() throws IOException {
        byte[] largeExcelData = createLargeTestExcelData();
        MockMultipartFile mockFile = new MockMultipartFile(
            "large", "large.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            largeExcelData
        );

        AtomicInteger totalProcessed = new AtomicInteger(0);
        AtomicInteger batchCount = new AtomicInteger(0);

        excelService.importLargeExcel(
            mockFile, 
            TestUser.class, 
            100, // 每页100条
            (data) -> {
                batchCount.incrementAndGet();
                totalProcessed.addAndGet(data.size());
                System.out.println("处理第 " + batchCount.get() + " 批，" + data.size() + " 条记录");
            }
        );

        assertTrue(totalProcessed.get() > 0);
        assertTrue(batchCount.get() > 0);
        System.out.println("总共处理 " + totalProcessed.get() + " 条记录，分 " + batchCount.get() + " 批");
    }

    @Test
    @DisplayName("生成Excel模板测试")
    void testGenerateTemplate() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        excelService.generateTemplate(response, TestUser.class, "用户导入模板");

        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertNotNull(response.getHeader("Content-disposition"));
        assertTrue(response.getHeader("Content-disposition").contains("用户导入模板_模板"));

        // 模板文件应该有内容（至少包含表头）
        assertTrue(response.getContentAsByteArray().length > 0);
        
        System.out.println("模板文件大小: " + response.getContentAsByteArray().length + " bytes");
    }

    @Test
    @DisplayName("Excel导入结果详细信息测试")
    void testImportResultDetails() throws IOException {
        // 创建包含错误数据的Excel
        byte[] excelWithErrors = createExcelWithValidationErrors();
        MockMultipartFile mockFile = new MockMultipartFile(
            "errors", "errors.xlsx", 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
            excelWithErrors
        );

        ExcelImportResult<TestUser> result = excelService.importExcel(mockFile, TestUser.class);

        assertNotNull(result);
        
        // 打印详细结果
        System.out.println("导入结果摘要:");
        System.out.println("总记录数: " + result.getTotalCount());
        System.out.println("成功记录数: " + result.getSuccessCount());
        System.out.println("失败记录数: " + result.getFailCount());
        System.out.println("成功率: " + String.format("%.2f%%", result.getSuccessRate()));
        System.out.println("错误摘要: " + result.getErrorSummary());

        if (result.hasError()) {
            result.getErrorMessages().forEach((row, error) -> 
                System.out.println("第" + row + "行错误: " + error));
        }
    }

    // 辅助方法：创建测试Excel数据
    private byte[] createTestExcelData() throws IOException {
        List<TestUser> testData = Arrays.asList(
            new TestUser("测试用户1", 25, "test1@example.com", LocalDateTime.now()),
            new TestUser("测试用户2", 30, "test2@example.com", LocalDateTime.now()),
            new TestUser("测试用户3", 28, "test3@example.com", LocalDateTime.now())
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        excelService.exportExcel(response, testData, TestUser.class, "测试数据");
        return response.getContentAsByteArray();
    }

    // 辅助方法：创建大量测试数据的Excel
    private byte[] createLargeTestExcelData() throws IOException {
        List<TestUser> largeTestData = new java.util.ArrayList<>(Arrays.asList(
            new TestUser("大数据用户1", 25, "large1@example.com", LocalDateTime.now()),
            new TestUser("大数据用户2", 30, "large2@example.com", LocalDateTime.now()),
            new TestUser("大数据用户3", 28, "large3@example.com", LocalDateTime.now()),
            new TestUser("大数据用户4", 32, "large4@example.com", LocalDateTime.now()),
            new TestUser("大数据用户5", 27, "large5@example.com", LocalDateTime.now())
        ));

        // 复制数据以模拟大文件
        for (int i = 0; i < 10; i++) {
            largeTestData.add(new TestUser("批量用户" + i, 25 + i, "batch" + i + "@example.com", LocalDateTime.now()));
        }

        MockHttpServletResponse response = new MockHttpServletResponse();
        excelService.exportExcel(response, largeTestData, TestUser.class, "大数据测试");
        return response.getContentAsByteArray();
    }

    // 辅助方法：创建包含验证错误的Excel数据
    private byte[] createExcelWithValidationErrors() throws IOException {
        List<TestUser> errorData = Arrays.asList(
            new TestUser("正常用户", 25, "normal@example.com", LocalDateTime.now()),
            new TestUser("", 30, "empty_name@example.com", LocalDateTime.now()), // 姓名为空
            new TestUser("负年龄用户", -5, "negative@example.com", LocalDateTime.now()), // 年龄为负
            new TestUser("正常用户2", 28, "normal2@example.com", LocalDateTime.now())
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        excelService.exportExcel(response, errorData, TestUser.class, "错误测试数据");
        return response.getContentAsByteArray();
    }
}