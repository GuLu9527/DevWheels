package com.dw.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("加密工具类测试")
public class EncryptUtilsTest {

    @Test
    @DisplayName("MD5加密测试")
    void testMd5() {
        String text = "hello world";
        String md5Hash = EncryptUtils.md5(text);
        
        assertNotNull(md5Hash);
        assertEquals(32, md5Hash.length()); // MD5哈希值长度为32个字符
        assertEquals("5eb63bbbe01eeed093cb22bb8f5acdc3", md5Hash);
        
        // 相同输入应该产生相同的哈希值
        String md5Hash2 = EncryptUtils.md5(text);
        assertEquals(md5Hash, md5Hash2);
        
        // 不同输入应该产生不同的哈希值
        String differentText = "hello world!";
        String differentHash = EncryptUtils.md5(differentText);
        assertNotEquals(md5Hash, differentHash);
        
        // null和空字符串测试
        assertNull(EncryptUtils.md5(null));
        assertEquals(EncryptUtils.md5(""), EncryptUtils.md5(""));
    }
    
    @Test
    @DisplayName("SHA256加密测试")
    void testSha256() {
        String text = "hello world";
        String sha256Hash = EncryptUtils.sha256(text);
        
        assertNotNull(sha256Hash);
        assertEquals(64, sha256Hash.length()); // SHA256哈希值长度为64个字符
        assertEquals("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9", sha256Hash);
        
        // 相同输入应该产生相同的哈希值
        String sha256Hash2 = EncryptUtils.sha256(text);
        assertEquals(sha256Hash, sha256Hash2);
        
        // 不同输入应该产生不同的哈希值
        String differentText = "hello world!";
        String differentHash = EncryptUtils.sha256(differentText);
        assertNotEquals(sha256Hash, differentHash);
        
        // null测试
        assertNull(EncryptUtils.sha256(null));
    }
    
    @Test
    @DisplayName("SHA512加密测试")
    void testSha512() {
        String text = "hello world";
        String sha512Hash = EncryptUtils.sha512(text);
        
        assertNotNull(sha512Hash);
        assertEquals(128, sha512Hash.length()); // SHA512哈希值长度为128个字符
        
        // 验证一致性
        String sha512Hash2 = EncryptUtils.sha512(text);
        assertEquals(sha512Hash, sha512Hash2);
        
        // 验证唯一性
        String differentHash = EncryptUtils.sha512("hello world!");
        assertNotEquals(sha512Hash, differentHash);
        
        // null测试
        assertNull(EncryptUtils.sha512(null));
    }
    
    @Test
    @DisplayName("Base64编码解码测试")
    void testBase64() {
        String originalText = "hello world 你好世界";
        
        // 编码
        String encoded = EncryptUtils.base64Encode(originalText);
        assertNotNull(encoded);
        assertNotEquals(originalText, encoded);
        
        // 解码
        String decoded = EncryptUtils.base64Decode(encoded);
        assertEquals(originalText, decoded);
        
        // 特殊字符测试
        String specialText = "!@#$%^&*()_+{}|:<>?[]\\;'\",./ 中文测试";
        String encodedSpecial = EncryptUtils.base64Encode(specialText);
        String decodedSpecial = EncryptUtils.base64Decode(encodedSpecial);
        assertEquals(specialText, decodedSpecial);
        
        // null和空字符串测试
        assertNull(EncryptUtils.base64Encode(null));
        assertEquals("", EncryptUtils.base64Encode(""));
        assertNull(EncryptUtils.base64Decode(null));
        assertEquals("", EncryptUtils.base64Decode(""));
        
        System.out.println("原文: " + originalText);
        System.out.println("编码: " + encoded);
        System.out.println("解码: " + decoded);
    }
    
    @Test
    @DisplayName("盐生成测试")
    void testGenerateSalt() {
        String salt1 = EncryptUtils.generateSalt();
        String salt2 = EncryptUtils.generateSalt();
        String salt3 = EncryptUtils.generateSalt();
        
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotNull(salt3);
        
        // 盐值长度应该为32个字符（16字节的十六进制表示）
        assertEquals(32, salt1.length());
        assertEquals(32, salt2.length());
        assertEquals(32, salt3.length());
        
        // 每次生成的盐值应该不同
        assertNotEquals(salt1, salt2);
        assertNotEquals(salt2, salt3);
        assertNotEquals(salt1, salt3);
        
        // 验证是十六进制字符
        assertTrue(salt1.matches("^[0-9a-f]{32}$"));
        assertTrue(salt2.matches("^[0-9a-f]{32}$"));
        assertTrue(salt3.matches("^[0-9a-f]{32}$"));
        
        System.out.println("生成的盐值: " + salt1 + ", " + salt2 + ", " + salt3);
    }
    
    @Test
    @DisplayName("带盐加密测试")
    void testSaltedEncryption() {
        String password = "myPassword123";
        String salt = EncryptUtils.generateSalt();
        
        // MD5加盐
        String md5WithSalt = EncryptUtils.md5WithSalt(password, salt);
        assertNotNull(md5WithSalt);
        assertEquals(32, md5WithSalt.length());
        
        // 相同密码和盐应该产生相同结果
        String md5WithSalt2 = EncryptUtils.md5WithSalt(password, salt);
        assertEquals(md5WithSalt, md5WithSalt2);
        
        // 不同盐应该产生不同结果
        String differentSalt = EncryptUtils.generateSalt();
        String md5WithDifferentSalt = EncryptUtils.md5WithSalt(password, differentSalt);
        assertNotEquals(md5WithSalt, md5WithDifferentSalt);
        
        // SHA256加盐
        String sha256WithSalt = EncryptUtils.sha256WithSalt(password, salt);
        assertNotNull(sha256WithSalt);
        assertEquals(64, sha256WithSalt.length());
        
        // 不同加密算法应该产生不同结果
        assertNotEquals(md5WithSalt, sha256WithSalt);
    }
    
    @Test
    @DisplayName("密码验证测试")
    void testPasswordVerification() {
        String password = "mySecretPassword";
        String salt = EncryptUtils.generateSalt();
        String hashedPassword = EncryptUtils.sha256WithSalt(password, salt);
        
        // 正确密码验证
        assertTrue(EncryptUtils.verifyPassword(password, salt, hashedPassword));
        
        // 错误密码验证
        assertFalse(EncryptUtils.verifyPassword("wrongPassword", salt, hashedPassword));
        
        // 错误盐验证
        String wrongSalt = EncryptUtils.generateSalt();
        assertFalse(EncryptUtils.verifyPassword(password, wrongSalt, hashedPassword));
        
        // null参数测试
        assertFalse(EncryptUtils.verifyPassword(null, salt, hashedPassword));
        assertFalse(EncryptUtils.verifyPassword(password, null, hashedPassword));
        assertFalse(EncryptUtils.verifyPassword(password, salt, null));
    }
    
    @Test
    @DisplayName("密码哈希生成测试")
    void testHashPassword() {
        String password = "testPassword123";
        String[] result = EncryptUtils.hashPassword(password);
        
        assertNotNull(result);
        assertEquals(2, result.length);
        
        String salt = result[0];
        String hash = result[1];
        
        assertNotNull(salt);
        assertNotNull(hash);
        assertEquals(32, salt.length()); // 盐值长度
        assertEquals(64, hash.length()); // SHA256哈希值长度
        
        // 验证生成的哈希值正确
        assertTrue(EncryptUtils.verifyPassword(password, salt, hash));
        
        // 多次生成应该产生不同的盐值和哈希值
        String[] result2 = EncryptUtils.hashPassword(password);
        assertNotEquals(result[0], result2[0]); // 不同的盐值
        assertNotEquals(result[1], result2[1]); // 不同的哈希值
        
        System.out.println("密码: " + password);
        System.out.println("盐值: " + salt);
        System.out.println("哈希: " + hash);
    }
    
    @Test
    @DisplayName("加密性能测试")
    void testEncryptionPerformance() {
        String text = "performance test string";
        int iterations = 10000;
        
        // MD5性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            EncryptUtils.md5(text);
        }
        long md5Time = System.currentTimeMillis() - startTime;
        
        // SHA256性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            EncryptUtils.sha256(text);
        }
        long sha256Time = System.currentTimeMillis() - startTime;
        
        // Base64编码性能测试
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            EncryptUtils.base64Encode(text);
        }
        long base64Time = System.currentTimeMillis() - startTime;
        
        System.out.println("MD5加密 " + iterations + " 次耗时: " + md5Time + "ms");
        System.out.println("SHA256加密 " + iterations + " 次耗时: " + sha256Time + "ms");
        System.out.println("Base64编码 " + iterations + " 次耗时: " + base64Time + "ms");
        
        // 性能要求
        assertTrue(md5Time < 3000, "MD5加密性能应该在3秒内完成");
        assertTrue(sha256Time < 3000, "SHA256加密性能应该在3秒内完成");
        assertTrue(base64Time < 1000, "Base64编码性能应该在1秒内完成");
    }
    
    @Test
    @DisplayName("安全性测试")
    void testSecurity() {
        String password = "commonPassword";
        
        // 测试彩虹表攻击防护（使用盐值）
        String[] hash1 = EncryptUtils.hashPassword(password);
        String[] hash2 = EncryptUtils.hashPassword(password);
        
        // 相同密码应该产生不同的哈希值（由于盐值不同）
        assertNotEquals(hash1[1], hash2[1]);
        
        // 但验证都应该成功
        assertTrue(EncryptUtils.verifyPassword(password, hash1[0], hash1[1]));
        assertTrue(EncryptUtils.verifyPassword(password, hash2[0], hash2[1]));
    }
}