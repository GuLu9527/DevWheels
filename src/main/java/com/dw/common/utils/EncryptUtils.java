package com.dw.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具类
 */
@Slf4j
public class EncryptUtils {
    
    private static final String MD5 = "MD5";
    private static final String SHA256 = "SHA-256";
    private static final String SHA512 = "SHA-512";
    
    /**
     * MD5加密
     */
    public static String md5(String text) {
        return encrypt(text, MD5);
    }
    
    /**
     * SHA256加密
     */
    public static String sha256(String text) {
        return encrypt(text, SHA256);
    }
    
    /**
     * SHA512加密
     */
    public static String sha512(String text) {
        return encrypt(text, SHA512);
    }
    
    /**
     * 通用加密方法
     */
    private static String encrypt(String text, String algorithm) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            log.error("{}加密失败: {}", algorithm, e.getMessage());
            return null;
        }
    }
    
    /**
     * 字节数组转16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Base64编码
     */
    public static String base64Encode(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base64解码
     */
    public static String base64Decode(String encodedText) {
        if (StringUtils.isEmpty(encodedText)) {
            return encodedText;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64解码失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 生成随机盐
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }
    
    /**
     * 带盐MD5加密
     */
    public static String md5WithSalt(String text, String salt) {
        return md5(text + salt);
    }
    
    /**
     * 带盐SHA256加密
     */
    public static String sha256WithSalt(String text, String salt) {
        return sha256(text + salt);
    }
    
    /**
     * 验证密码（带盐）
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(salt) || StringUtils.isEmpty(hashedPassword)) {
            return false;
        }
        String encrypted = sha256WithSalt(password, salt);
        return StringUtils.equals(encrypted, hashedPassword);
    }
    
    /**
     * 生成密码哈希（包含盐）
     */
    public static String[] hashPassword(String password) {
        String salt = generateSalt();
        String hash = sha256WithSalt(password, salt);
        return new String[]{salt, hash};
    }
}