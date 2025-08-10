package com.dw.common.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@DisplayName("本地文件存储测试")
public class LocalFileStorageTest {

    @TempDir
    Path tempDir;

    private LocalFileStorage localStorage;
    private final String urlPrefix = "/files";

    @BeforeEach
    void setUp() {
        localStorage = new LocalFileStorage(tempDir.toString(), urlPrefix);
    }

    @Test
    @DisplayName("上传文件成功测试")
    void testUploadFileSuccess() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
            "test", "test.txt", "text/plain", "Hello World".getBytes()
        );

        String storagePath = "test/test.txt";
        String url = localStorage.upload(mockFile, storagePath);

        assertNotNull(url);
        assertEquals(urlPrefix + "/" + storagePath, url);

        // 验证文件确实被保存
        Path filePath = tempDir.resolve(storagePath);
        assertTrue(Files.exists(filePath));
        
        String content = Files.readString(filePath);
        assertEquals("Hello World", content);
    }

    @Test
    @DisplayName("上传文件覆盖测试")
    void testUploadFileOverwrite() throws IOException {
        String storagePath = "test/overwrite.txt";
        
        // 第一次上传
        MockMultipartFile file1 = new MockMultipartFile(
            "test", "test.txt", "text/plain", "Original Content".getBytes()
        );
        String url1 = localStorage.upload(file1, storagePath);
        assertNotNull(url1);
        
        // 第二次上传（覆盖）
        MockMultipartFile file2 = new MockMultipartFile(
            "test", "test.txt", "text/plain", "New Content".getBytes()
        );
        String url2 = localStorage.upload(file2, storagePath);
        assertNotNull(url2);
        assertEquals(url1, url2);
        
        // 验证内容被覆盖
        Path filePath = tempDir.resolve(storagePath);
        String content = Files.readString(filePath);
        assertEquals("New Content", content);
    }

    @Test
    @DisplayName("上传空文件测试")
    void testUploadEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "empty", "empty.txt", "text/plain", new byte[0]
        );

        String url = localStorage.upload(emptyFile, "test/empty.txt");
        assertNull(url);
    }

    @Test
    @DisplayName("上传null文件测试")
    void testUploadNullFile() {
        String url = localStorage.upload(null, "test/null.txt");
        assertNull(url);
    }

    @Test
    @DisplayName("上传流文件成功测试")
    void testUploadStreamSuccess() throws IOException {
        String content = "Stream Content";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String storagePath = "stream/test.txt";

        String url = localStorage.upload(inputStream, storagePath, "text/plain");

        assertNotNull(url);
        assertEquals(urlPrefix + "/" + storagePath, url);

        // 验证文件内容
        Path filePath = tempDir.resolve(storagePath);
        assertTrue(Files.exists(filePath));
        
        String savedContent = Files.readString(filePath);
        assertEquals(content, savedContent);
    }

    @Test
    @DisplayName("上传null流测试")
    void testUploadNullStream() {
        String url = localStorage.upload(null, "test/null.txt", "text/plain");
        assertNull(url);
    }

    @Test
    @DisplayName("上传空路径测试")
    void testUploadEmptyPath() {
        InputStream inputStream = new ByteArrayInputStream("content".getBytes());
        String url = localStorage.upload(inputStream, "", "text/plain");
        assertNull(url);
    }

    @Test
    @DisplayName("删除文件成功测试")
    void testDeleteFileSuccess() throws IOException {
        // 首先创建文件
        String storagePath = "delete/test.txt";
        Path filePath = tempDir.resolve(storagePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "Delete Me");

        // 验证文件存在
        assertTrue(Files.exists(filePath));

        // 删除文件
        boolean deleted = localStorage.delete(storagePath);

        assertTrue(deleted);
        assertFalse(Files.exists(filePath));
    }

    @Test
    @DisplayName("删除不存在的文件测试")
    void testDeleteNonExistentFile() {
        boolean deleted = localStorage.delete("nonexistent/file.txt");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("删除空路径测试")
    void testDeleteEmptyPath() {
        boolean deleted = localStorage.delete("");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("删除null路径测试")
    void testDeleteNullPath() {
        boolean deleted = localStorage.delete(null);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("获取文件URL测试")
    void testGetUrl() {
        String path = "images/2024/01/15/test.jpg";
        String url = localStorage.getUrl(path);
        
        assertEquals(urlPrefix + "/" + path, url);
    }

    @Test
    @DisplayName("获取带斜杠开头路径的URL测试")
    void testGetUrlWithLeadingSlash() {
        String path = "/images/2024/01/15/test.jpg";
        String url = localStorage.getUrl(path);
        
        assertEquals(urlPrefix + path, url);
    }

    @Test
    @DisplayName("获取空路径URL测试")
    void testGetUrlEmptyPath() {
        String url = localStorage.getUrl("");
        assertNull(url);
    }

    @Test
    @DisplayName("获取null路径URL测试")
    void testGetUrlNullPath() {
        String url = localStorage.getUrl(null);
        assertNull(url);
    }

    @Test
    @DisplayName("检查文件存在测试")
    void testFileExists() throws IOException {
        // 创建测试文件
        String storagePath = "exists/test.txt";
        Path filePath = tempDir.resolve(storagePath);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "Exists Test");

        assertTrue(localStorage.exists(storagePath));
    }

    @Test
    @DisplayName("检查文件不存在测试")
    void testFileNotExists() {
        assertFalse(localStorage.exists("nonexistent/file.txt"));
    }

    @Test
    @DisplayName("检查空路径存在测试")
    void testExistsEmptyPath() {
        assertFalse(localStorage.exists(""));
    }

    @Test
    @DisplayName("检查null路径存在测试")
    void testExistsNullPath() {
        assertFalse(localStorage.exists(null));
    }

    @Test
    @DisplayName("深层目录结构创建测试")
    void testDeepDirectoryCreation() throws IOException {
        String deepPath = "level1/level2/level3/level4/deep.txt";
        MockMultipartFile mockFile = new MockMultipartFile(
            "deep", "deep.txt", "text/plain", "Deep Directory Test".getBytes()
        );

        String url = localStorage.upload(mockFile, deepPath);

        assertNotNull(url);
        Path filePath = tempDir.resolve(deepPath);
        assertTrue(Files.exists(filePath));
        
        String content = Files.readString(filePath);
        assertEquals("Deep Directory Test", content);
    }

    @Test
    @DisplayName("特殊字符文件名测试")
    void testSpecialCharacterFilenames() throws IOException {
        String specialPath = "special/文件名 with spaces & symbols!@#$%^&().txt";
        MockMultipartFile mockFile = new MockMultipartFile(
            "special", "special.txt", "text/plain", "Special Characters".getBytes()
        );

        String url = localStorage.upload(mockFile, specialPath);

        assertNotNull(url);
        assertTrue(localStorage.exists(specialPath));
        
        // 测试删除
        assertTrue(localStorage.delete(specialPath));
        assertFalse(localStorage.exists(specialPath));
    }

    @Test
    @DisplayName("大文件上传测试")
    void testLargeFileUpload() throws IOException {
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        MockMultipartFile largeFile = new MockMultipartFile(
            "large", "large.dat", "application/octet-stream", largeContent
        );

        String storagePath = "large/large.dat";
        String url = localStorage.upload(largeFile, storagePath);

        assertNotNull(url);
        
        Path filePath = tempDir.resolve(storagePath);
        assertTrue(Files.exists(filePath));
        assertEquals(largeContent.length, Files.size(filePath));
    }

    @Test
    @DisplayName("并发文件操作测试")
    void testConcurrentFileOperations() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    MockMultipartFile file = new MockMultipartFile(
                        "concurrent", 
                        "file" + threadIndex + ".txt", 
                        "text/plain", 
                        ("Content " + threadIndex).getBytes()
                    );

                    String path = "concurrent/file" + threadIndex + ".txt";
                    String url = localStorage.upload(file, path);
                    results[threadIndex] = url != null && localStorage.exists(path);
                } catch (Exception e) {
                    results[threadIndex] = false;
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证所有操作都成功
        for (boolean result : results) {
            assertTrue(result);
        }
    }
}