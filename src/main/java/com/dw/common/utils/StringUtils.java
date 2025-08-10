package com.dw.common.utils;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils {
    
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 去除字符串两端空格，null安全
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * 字符串为空时返回默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
    
    /**
     * 判断字符串是否相等（null安全）
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }
    
    /**
     * 忽略大小写判断字符串是否相等
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }
    
    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
    
    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalize = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                capitalize = true;
            } else {
                if (capitalize) {
                    result.append(Character.toUpperCase(c));
                    capitalize = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }
    
    /**
     * 驼峰转下划线
     */
    public static String toUnderlineCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * 拼接字符串
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }
    
    /**
     * 检查字符串是否匹配正则表达式
     */
    public static boolean matches(String str, String regex) {
        return isNotEmpty(str) && Pattern.matches(regex, str);
    }
    
    /**
     * 手机号脱敏
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    /**
     * 身份证号脱敏
     */
    public static String maskIdCard(String idCard) {
        if (isEmpty(idCard) || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }
}