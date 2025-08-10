package com.dw.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);
    
    /**
     * 获取当前日期字符串
     */
    public static String nowDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
    
    /**
     * 获取当前日期时间字符串
     */
    public static String nowDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * 获取当前时间字符串
     */
    public static String nowTime() {
        return LocalTime.now().format(TIME_FORMATTER);
    }
    
    /**
     * LocalDateTime转字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * LocalDateTime转字符串（自定义格式）
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime == null ? null : dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * LocalDate转字符串
     */
    public static String format(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }
    
    /**
     * LocalDate转字符串（自定义格式）
     */
    public static String format(LocalDate date, String pattern) {
        return date == null ? null : date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 字符串转LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return StringUtils.isEmpty(dateTimeStr) ? null : LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }
    
    /**
     * 字符串转LocalDateTime（自定义格式）
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        return StringUtils.isEmpty(dateTimeStr) ? null : LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 字符串转LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return StringUtils.isEmpty(dateStr) ? null : LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    /**
     * 字符串转LocalDate（自定义格式）
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        return StringUtils.isEmpty(dateStr) ? null : LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 获取当天开始时间
     */
    public static LocalDateTime startOfDay() {
        return LocalDate.now().atStartOfDay();
    }
    
    /**
     * 获取当天结束时间
     */
    public static LocalDateTime endOfDay() {
        return LocalDate.now().atTime(23, 59, 59, 999999999);
    }
    
    /**
     * 获取指定日期的开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }
    
    /**
     * 获取指定日期的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.atTime(23, 59, 59, 999999999);
    }
    
    /**
     * 计算两个日期之间的天数
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * 计算两个时间之间的小时数
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算两个时间之间的分钟数
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }
    
    /**
     * 判断是否是今天
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }
    
    /**
     * 判断是否是今天
     */
    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime != null && dateTime.toLocalDate().equals(LocalDate.now());
    }
    
    /**
     * 获取年龄
     */
    public static int getAge(LocalDate birthDate) {
        return birthDate == null ? 0 : (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
}