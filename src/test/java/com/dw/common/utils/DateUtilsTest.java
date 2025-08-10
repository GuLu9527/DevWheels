package com.dw.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@DisplayName("日期工具类测试")
public class DateUtilsTest {

    @Test
    @DisplayName("当前日期时间获取测试")
    void testNowMethods() {
        String nowDate = DateUtils.nowDate();
        String nowDateTime = DateUtils.nowDateTime();
        String nowTime = DateUtils.nowTime();
        
        assertNotNull(nowDate);
        assertNotNull(nowDateTime);
        assertNotNull(nowTime);
        
        // 验证格式
        assertTrue(nowDate.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue(nowDateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        assertTrue(nowTime.matches("\\d{2}:\\d{2}:\\d{2}"));
        
        System.out.println("当前日期: " + nowDate);
        System.out.println("当前日期时间: " + nowDateTime);
        System.out.println("当前时间: " + nowTime);
    }
    
    @Test
    @DisplayName("LocalDateTime格式化测试")
    void testFormatLocalDateTime() {
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        
        // 默认格式化
        String formatted = DateUtils.format(testDateTime);
        assertEquals("2024-01-15 14:30:45", formatted);
        
        // 自定义格式化
        String customFormatted = DateUtils.format(testDateTime, "yyyy/MM/dd HH:mm");
        assertEquals("2024/01/15 14:30", customFormatted);
        
        // null测试
        assertNull(DateUtils.format((LocalDateTime) null));
        assertNull(DateUtils.format((LocalDateTime) null, "yyyy-MM-dd"));
    }
    
    @Test
    @DisplayName("LocalDate格式化测试")
    void testFormatLocalDate() {
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        
        // 默认格式化
        String formatted = DateUtils.format(testDate);
        assertEquals("2024-01-15", formatted);
        
        // 自定义格式化
        String customFormatted = DateUtils.format(testDate, "yyyy/MM/dd");
        assertEquals("2024/01/15", customFormatted);
        
        // null测试
        assertNull(DateUtils.format((LocalDate) null));
        assertNull(DateUtils.format((LocalDateTime) null, "yyyy-MM-dd"));
    }
    
    @Test
    @DisplayName("字符串解析测试")
    void testParseMethods() {
        // 解析日期时间
        LocalDateTime dateTime = DateUtils.parseDateTime("2024-01-15 14:30:45");
        assertNotNull(dateTime);
        assertEquals(2024, dateTime.getYear());
        assertEquals(1, dateTime.getMonthValue());
        assertEquals(15, dateTime.getDayOfMonth());
        assertEquals(14, dateTime.getHour());
        assertEquals(30, dateTime.getMinute());
        assertEquals(45, dateTime.getSecond());
        
        // 自定义格式解析
        LocalDateTime customDateTime = DateUtils.parseDateTime("2024/01/15 14:30", "yyyy/MM/dd HH:mm");
        assertNotNull(customDateTime);
        assertEquals(2024, customDateTime.getYear());
        assertEquals(1, customDateTime.getMonthValue());
        assertEquals(15, customDateTime.getDayOfMonth());
        
        // 解析日期
        LocalDate date = DateUtils.parseDate("2024-01-15");
        assertNotNull(date);
        assertEquals(2024, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());
        
        // null和空字符串测试
        assertNull(DateUtils.parseDateTime(null));
        assertNull(DateUtils.parseDateTime(""));
        assertNull(DateUtils.parseDate(null));
        assertNull(DateUtils.parseDate(""));
    }
    
    @Test
    @DisplayName("Date与LocalDateTime转换测试")
    void testDateConversion() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        
        // LocalDateTime转Date
        Date date = DateUtils.toDate(localDateTime);
        assertNotNull(date);
        
        // Date转LocalDateTime
        LocalDateTime convertedBack = DateUtils.toLocalDateTime(date);
        assertNotNull(convertedBack);
        assertEquals(localDateTime.getYear(), convertedBack.getYear());
        assertEquals(localDateTime.getMonthValue(), convertedBack.getMonthValue());
        assertEquals(localDateTime.getDayOfMonth(), convertedBack.getDayOfMonth());
        assertEquals(localDateTime.getHour(), convertedBack.getHour());
        assertEquals(localDateTime.getMinute(), convertedBack.getMinute());
        assertEquals(localDateTime.getSecond(), convertedBack.getSecond());
        
        // null测试
        assertNull(DateUtils.toDate(null));
        assertNull(DateUtils.toLocalDateTime(null));
    }
    
    @Test
    @DisplayName("一天开始和结束时间测试")
    void testStartAndEndOfDay() {
        LocalDateTime startOfToday = DateUtils.startOfDay();
        LocalDateTime endOfToday = DateUtils.endOfDay();
        
        assertNotNull(startOfToday);
        assertNotNull(endOfToday);
        
        // 验证开始时间
        assertEquals(0, startOfToday.getHour());
        assertEquals(0, startOfToday.getMinute());
        assertEquals(0, startOfToday.getSecond());
        assertEquals(0, startOfToday.getNano());
        
        // 验证结束时间
        assertEquals(23, endOfToday.getHour());
        assertEquals(59, endOfToday.getMinute());
        assertEquals(59, endOfToday.getSecond());
        
        // 指定日期测试
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        LocalDateTime startOfDate = DateUtils.startOfDay(testDate);
        LocalDateTime endOfDate = DateUtils.endOfDay(testDate);
        
        assertEquals(2024, startOfDate.getYear());
        assertEquals(1, startOfDate.getMonthValue());
        assertEquals(15, startOfDate.getDayOfMonth());
        assertEquals(0, startOfDate.getHour());
        
        assertEquals(2024, endOfDate.getYear());
        assertEquals(1, endOfDate.getMonthValue());
        assertEquals(15, endOfDate.getDayOfMonth());
        assertEquals(23, endOfDate.getHour());
        
        System.out.println("今天开始: " + DateUtils.format(startOfToday));
        System.out.println("今天结束: " + DateUtils.format(endOfToday));
    }
    
    @Test
    @DisplayName("日期时间差计算测试")
    void testDateTimeDifference() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 15);
        
        long days = DateUtils.daysBetween(startDate, endDate);
        assertEquals(14, days);
        
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 1, 1, 14, 30);
        
        long hours = DateUtils.hoursBetween(startDateTime, endDateTime);
        assertEquals(4, hours);
        
        long minutes = DateUtils.minutesBetween(startDateTime, endDateTime);
        assertEquals(270, minutes); // 4小时30分钟 = 270分钟
    }
    
    @Test
    @DisplayName("今天判断测试")
    void testIsToday() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        
        assertTrue(DateUtils.isToday(today));
        assertFalse(DateUtils.isToday(yesterday));
        assertFalse(DateUtils.isToday(tomorrow));
        assertFalse(DateUtils.isToday((LocalDate) null));
        
        LocalDateTime todayDateTime = LocalDateTime.now();
        LocalDateTime yesterdayDateTime = todayDateTime.minusDays(1);
        
        assertTrue(DateUtils.isToday(todayDateTime));
        assertFalse(DateUtils.isToday(yesterdayDateTime));
        assertFalse(DateUtils.isToday((LocalDateTime) null));
    }
    
    @Test
    @DisplayName("年龄计算测试")
    void testGetAge() {
        LocalDate birthDate1 = LocalDate.now().minusYears(25).minusDays(1);
        LocalDate birthDate2 = LocalDate.now().minusYears(30).plusDays(1);
        LocalDate birthDate3 = LocalDate.now().minusYears(18);
        
        assertEquals(25, DateUtils.getAge(birthDate1));
        assertEquals(29, DateUtils.getAge(birthDate2)); // 还没到30岁生日
        assertEquals(18, DateUtils.getAge(birthDate3));
        assertEquals(0, DateUtils.getAge(null));
        
        // 测试未来日期（应该返回0或负数，这里假设返回0）
        LocalDate futureDate = LocalDate.now().plusYears(5);
        assertTrue(DateUtils.getAge(futureDate) <= 0);
    }
    
    @Test
    @DisplayName("日期格式化性能测试")
    void testFormattingPerformance() {
        int iterations = 10000;
        LocalDateTime testDateTime = LocalDateTime.now();
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            DateUtils.format(testDateTime);
        }
        long formatTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            DateUtils.parseDateTime("2024-01-15 14:30:45");
        }
        long parseTime = System.currentTimeMillis() - startTime;
        
        System.out.println("格式化 " + iterations + " 次耗时: " + formatTime + "ms");
        System.out.println("解析 " + iterations + " 次耗时: " + parseTime + "ms");
        
        // 性能要求
        assertTrue(formatTime < 1000, "格式化性能应该在1秒内完成");
        assertTrue(parseTime < 1000, "解析性能应该在1秒内完成");
    }
}