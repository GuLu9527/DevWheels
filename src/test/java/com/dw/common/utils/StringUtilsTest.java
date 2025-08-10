package com.dw.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@DisplayName("字符串工具类测试")
public class StringUtilsTest {

    @Test
    @DisplayName("字符串为空判断测试")
    void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty("   "));
        
        assertFalse(StringUtils.isEmpty("hello"));
        assertFalse(StringUtils.isEmpty(" hello "));
    }
    
    @Test
    @DisplayName("字符串不为空判断测试")
    void testIsNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertFalse(StringUtils.isNotEmpty("   "));
        
        assertTrue(StringUtils.isNotEmpty("hello"));
        assertTrue(StringUtils.isNotEmpty(" hello "));
    }
    
    @Test
    @DisplayName("字符串空白判断测试")
    void testIsBlankAndNotBlank() {
        // isBlank测试
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertTrue(StringUtils.isBlank("\t\n\r"));
        
        assertFalse(StringUtils.isBlank("hello"));
        assertFalse(StringUtils.isBlank(" hello "));
        
        // isNotBlank测试
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank("   "));
        
        assertTrue(StringUtils.isNotBlank("hello"));
        assertTrue(StringUtils.isNotBlank(" hello "));
    }
    
    @Test
    @DisplayName("字符串修剪测试")
    void testTrim() {
        assertNull(StringUtils.trim(null));
        assertEquals("", StringUtils.trim(""));
        assertEquals("", StringUtils.trim("   "));
        assertEquals("hello", StringUtils.trim("  hello  "));
        assertEquals("hello world", StringUtils.trim("  hello world  "));
    }
    
    @Test
    @DisplayName("默认值测试")
    void testDefaultIfEmpty() {
        assertEquals("default", StringUtils.defaultIfEmpty(null, "default"));
        assertEquals("default", StringUtils.defaultIfEmpty("", "default"));
        assertEquals("default", StringUtils.defaultIfEmpty("   ", "default"));
        assertEquals("hello", StringUtils.defaultIfEmpty("hello", "default"));
    }
    
    @Test
    @DisplayName("字符串相等性测试")
    void testEquals() {
        assertTrue(StringUtils.equals(null, null));
        assertTrue(StringUtils.equals("hello", "hello"));
        
        assertFalse(StringUtils.equals(null, "hello"));
        assertFalse(StringUtils.equals("hello", null));
        assertFalse(StringUtils.equals("hello", "world"));
        assertFalse(StringUtils.equals("Hello", "hello"));
    }
    
    @Test
    @DisplayName("忽略大小写相等性测试")
    void testEqualsIgnoreCase() {
        assertTrue(StringUtils.equalsIgnoreCase(null, null));
        assertTrue(StringUtils.equalsIgnoreCase("hello", "hello"));
        assertTrue(StringUtils.equalsIgnoreCase("Hello", "hello"));
        assertTrue(StringUtils.equalsIgnoreCase("HELLO", "hello"));
        
        assertFalse(StringUtils.equalsIgnoreCase(null, "hello"));
        assertFalse(StringUtils.equalsIgnoreCase("hello", null));
        assertFalse(StringUtils.equalsIgnoreCase("hello", "world"));
    }
    
    @Test
    @DisplayName("首字母大小写测试")
    void testCapitalizeAndUncapitalize() {
        // capitalize测试
        assertNull(StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
        assertEquals("H", StringUtils.capitalize("h"));
        assertEquals("Hello", StringUtils.capitalize("hello"));
        assertEquals("Hello world", StringUtils.capitalize("hello world"));
        assertEquals("Hello", StringUtils.capitalize("Hello"));
        
        // uncapitalize测试
        assertNull(StringUtils.uncapitalize(null));
        assertEquals("", StringUtils.uncapitalize(""));
        assertEquals("h", StringUtils.uncapitalize("H"));
        assertEquals("hello", StringUtils.uncapitalize("Hello"));
        assertEquals("hello World", StringUtils.uncapitalize("Hello World"));
        assertEquals("hello", StringUtils.uncapitalize("hello"));
    }
    
    @Test
    @DisplayName("下划线转驼峰测试")
    void testToCamelCase() {
        assertNull(StringUtils.toCamelCase(null));
        assertEquals("", StringUtils.toCamelCase(""));
        assertEquals("hello", StringUtils.toCamelCase("hello"));
        assertEquals("helloWorld", StringUtils.toCamelCase("hello_world"));
        assertEquals("userNameId", StringUtils.toCamelCase("user_name_id"));
        assertEquals("userName", StringUtils.toCamelCase("user_name"));
        assertEquals("userId", StringUtils.toCamelCase("USER_ID"));
    }
    
    @Test
    @DisplayName("驼峰转下划线测试")
    void testToUnderlineCase() {
        assertNull(StringUtils.toUnderlineCase(null));
        assertEquals("", StringUtils.toUnderlineCase(""));
        assertEquals("hello", StringUtils.toUnderlineCase("hello"));
        assertEquals("hello_world", StringUtils.toUnderlineCase("helloWorld"));
        assertEquals("user_name_id", StringUtils.toUnderlineCase("userNameId"));
        assertEquals("user_name", StringUtils.toUnderlineCase("userName"));
        assertEquals("u_s_e_r_i_d", StringUtils.toUnderlineCase("USERID"));
    }
    
    @Test
    @DisplayName("集合拼接测试")
    void testJoin() {
        assertEquals("", StringUtils.join(null, ","));
        assertEquals("", StringUtils.join(Arrays.asList(), ","));
        
        List<String> list1 = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", StringUtils.join(list1, ","));
        assertEquals("a|b|c", StringUtils.join(list1, "|"));
        assertEquals("abc", StringUtils.join(list1, ""));
        
        List<Integer> list2 = Arrays.asList(1, 2, 3);
        assertEquals("1-2-3", StringUtils.join(list2, "-"));
    }
    
    @Test
    @DisplayName("正则匹配测试")
    void testMatches() {
        assertFalse(StringUtils.matches(null, "\\d+"));
        assertFalse(StringUtils.matches("", "\\d+"));
        
        assertTrue(StringUtils.matches("123", "\\d+"));
        assertTrue(StringUtils.matches("hello@example.com", ".+@.+\\..+"));
        
        assertFalse(StringUtils.matches("abc", "\\d+"));
        assertFalse(StringUtils.matches("hello", ".+@.+\\..+"));
    }
    
    @Test
    @DisplayName("手机号脱敏测试")
    void testMaskPhone() {
        assertNull(StringUtils.maskPhone(null));
        assertEquals("", StringUtils.maskPhone(""));
        assertEquals("123", StringUtils.maskPhone("123"));
        
        assertEquals("138****5678", StringUtils.maskPhone("13812345678"));
        assertEquals("186****9999", StringUtils.maskPhone("18611119999"));
        assertEquals("150****1234", StringUtils.maskPhone("15012341234"));
    }
    
    @Test
    @DisplayName("身份证号脱敏测试")
    void testMaskIdCard() {
        assertNull(StringUtils.maskIdCard(null));
        assertEquals("", StringUtils.maskIdCard(""));
        assertEquals("1234", StringUtils.maskIdCard("1234"));
        
        assertEquals("1234**********4567", StringUtils.maskIdCard("12345678901234567"));
        assertEquals("3301**********123X", StringUtils.maskIdCard("33010219901201123X"));
        assertEquals("4401**********4321", StringUtils.maskIdCard("440182199001014321"));
    }
    
    @Test
    @DisplayName("字符串性能测试")
    void testPerformance() {
        int iterations = 100000;
        String testStr = "hello_world_test_string";
        
        // 测试驼峰转换性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            StringUtils.toCamelCase(testStr);
        }
        long camelCaseTime = System.currentTimeMillis() - startTime;
        
        // 测试下划线转换性能
        String camelStr = "helloWorldTestString";
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            StringUtils.toUnderlineCase(camelStr);
        }
        long underlineCaseTime = System.currentTimeMillis() - startTime;
        
        System.out.println("驼峰转换 " + iterations + " 次耗时: " + camelCaseTime + "ms");
        System.out.println("下划线转换 " + iterations + " 次耗时: " + underlineCaseTime + "ms");
        
        // 性能要求：10万次转换应在1秒内完成
        assertTrue(camelCaseTime < 1000, "驼峰转换性能应该在1秒内完成");
        assertTrue(underlineCaseTime < 1000, "下划线转换性能应该在1秒内完成");
    }
}