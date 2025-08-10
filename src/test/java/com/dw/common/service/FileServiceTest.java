package com.dw.common.service;

import com.dw.common.storage.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
@DisplayName("文件服务测试")
public class FileServiceTest {

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private FileService fileService;

    private MockMultipartFile mockImageFile;
    private MockMultipartFile mockDocumentFile;
    private MockMultipartFile mockInvalidFile;

    @BeforeEach
    void setUp() {
        // 创建模拟的图片文件
        mockImageFile = new MockMultipartFile(
            "image", 
            "test.jpg", 
            "image/jpeg", 
            "fake image content".getBytes()
        );

        // 创建模拟的文档文件
        mockDocumentFile = new MockMultipartFile(
            "document",
            "test.pdf",
            "application/pdf",
            "fake pdf content".getBytes()
        );

        // 创建模拟的无效文件
        mockInvalidFile = new MockMultipartFile(
            "invalid",
            "test.exe",
            "application/x-executable",
            "fake executable content".getBytes()
        );
    }

    @Test
    @DisplayName("上传图片成功测试")
    void testUploadImageSuccess() {
        String expectedUrl = "http://localhost:8080/files/images/2024/01/15/uuid.jpg";
        when(fileStorage.upload(any(MultipartFile.class), anyString()))
            .thenReturn(expectedUrl);

        String actualUrl = fileService.uploadImage(mockImageFile);

        assertEquals(expectedUrl, actualUrl);
        verify(fileStorage, times(1)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("上传文档成功测试")
    void testUploadDocumentSuccess() {
        String expectedUrl = "http://localhost:8080/files/documents/2024/01/15/uuid.pdf";
        when(fileStorage.upload(any(MultipartFile.class), anyString()))
            .thenReturn(expectedUrl);

        String actualUrl = fileService.uploadDocument(mockDocumentFile);

        assertEquals(expectedUrl, actualUrl);
        verify(fileStorage, times(1)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("上传通用文件成功测试")
    void testUploadFileSuccess() {
        String expectedUrl = "http://localhost:8080/files/files/2024/01/15/uuid.jpg";
        when(fileStorage.upload(any(MultipartFile.class), anyString()))
            .thenReturn(expectedUrl);

        String actualUrl = fileService.uploadFile(mockImageFile);

        assertEquals(expectedUrl, actualUrl);
        verify(fileStorage, times(1)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("上传空文件失败测试")
    void testUploadEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("empty", "", "text/plain", new byte[0]);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> fileService.uploadImage(emptyFile)
        );
        assertEquals("文件不能为空", exception.getMessage());

        verify(fileStorage, never()).upload(any(), any());
    }

    @Test
    @DisplayName("上传null文件失败测试")
    void testUploadNullFile() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadImage(null)
        );
        assertEquals("文件不能为空", exception.getMessage());

        verify(fileStorage, never()).upload(any(), any());
    }

    @Test
    @DisplayName("上传超大文件失败测试")
    void testUploadOversizeFile() {
        // 创建超过10MB的模拟文件
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile largeFile = new MockMultipartFile(
            "large", "large.jpg", "image/jpeg", largeContent
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadImage(largeFile)
        );
        assertEquals("文件大小不能超过10MB", exception.getMessage());

        verify(fileStorage, never()).upload(any(), any());
    }

    @Test
    @DisplayName("上传不支持的图片类型失败测试")
    void testUploadUnsupportedImageType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadImage(mockInvalidFile)
        );
        assertEquals("不支持的文件类型: exe", exception.getMessage());

        verify(fileStorage, never()).upload(any(), any());
    }

    @Test
    @DisplayName("上传不支持的文档类型失败测试")
    void testUploadUnsupportedDocumentType() {
        MockMultipartFile invalidDoc = new MockMultipartFile(
            "invalid", "test.xyz", "application/xyz", "content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadDocument(invalidDoc)
        );
        assertEquals("不支持的文件类型: xyz", exception.getMessage());

        verify(fileStorage, never()).upload(any(), any());
    }

    @Test
    @DisplayName("上传无扩展名文件失败测试")
    void testUploadFileWithoutExtension() {
        MockMultipartFile noExtFile = new MockMultipartFile(
            "noext", "filename", "text/plain", "content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadImage(noExtFile)
        );
        assertEquals("文件扩展名不能为空", exception.getMessage());

        verify(fileStorage, never()).upload(any(), any());
    }

    @Test
    @DisplayName("文件存储失败测试")
    void testFileStorageFailure() {
        when(fileStorage.upload(any(MultipartFile.class), anyString()))
            .thenReturn(null);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> fileService.uploadImage(mockImageFile)
        );
        assertEquals("文件上传失败", exception.getMessage());

        verify(fileStorage, times(1)).upload(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("流式上传文件成功测试")
    void testUploadFileStream() {
        String expectedUrl = "http://localhost:8080/files/files/2024/01/15/uuid.txt";
        when(fileStorage.upload(any(InputStream.class), anyString(), anyString()))
            .thenReturn(expectedUrl);

        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());
        String actualUrl = fileService.uploadFile(inputStream, "test.txt", "text/plain");

        assertEquals(expectedUrl, actualUrl);
        verify(fileStorage, times(1)).upload(any(InputStream.class), anyString(), anyString());
    }

    @Test
    @DisplayName("流式上传null输入流失败测试")
    void testUploadNullInputStream() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadFile(null, "test.txt", "text/plain")
        );
        assertEquals("输入流和文件名不能为空", exception.getMessage());

        verify(fileStorage, never()).upload(any(InputStream.class), any(), any());
    }

    @Test
    @DisplayName("流式上传空文件名失败测试")
    void testUploadEmptyFilename() {
        InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileService.uploadFile(inputStream, "", "text/plain")
        );
        assertEquals("输入流和文件名不能为空", exception.getMessage());

        verify(fileStorage, never()).upload(any(InputStream.class), any(), any());
    }

    @Test
    @DisplayName("删除文件成功测试")
    void testDeleteFileSuccess() {
        String fileUrl = "http://localhost:8080/files/images/2024/01/15/test.jpg";
        when(fileStorage.delete(anyString())).thenReturn(true);

        boolean result = fileService.deleteFile(fileUrl);

        assertTrue(result);
        verify(fileStorage, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("删除文件失败测试")
    void testDeleteFileFailure() {
        String fileUrl = "http://localhost:8080/files/images/2024/01/15/test.jpg";
        when(fileStorage.delete(anyString())).thenReturn(false);

        boolean result = fileService.deleteFile(fileUrl);

        assertFalse(result);
        verify(fileStorage, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("删除空URL测试")
    void testDeleteEmptyUrl() {
        boolean result = fileService.deleteFile("");

        assertFalse(result);
        verify(fileStorage, never()).delete(any());
    }

    @Test
    @DisplayName("检查文件存在测试")
    void testFileExists() {
        String fileUrl = "http://localhost:8080/files/images/2024/01/15/test.jpg";
        when(fileStorage.exists(anyString())).thenReturn(true);

        boolean exists = fileService.fileExists(fileUrl);

        assertTrue(exists);
        verify(fileStorage, times(1)).exists(anyString());
    }

    @Test
    @DisplayName("检查文件不存在测试")
    void testFileNotExists() {
        String fileUrl = "http://localhost:8080/files/images/2024/01/15/test.jpg";
        when(fileStorage.exists(anyString())).thenReturn(false);

        boolean exists = fileService.fileExists(fileUrl);

        assertFalse(exists);
        verify(fileStorage, times(1)).exists(anyString());
    }

    @Test
    @DisplayName("检查空URL文件存在测试")
    void testCheckExistsEmptyUrl() {
        boolean exists = fileService.fileExists("");

        assertFalse(exists);
        verify(fileStorage, never()).exists(any());
    }

    @Test
    @DisplayName("支持的图片类型测试")
    void testSupportedImageTypes() {
        // 测试支持的图片类型
        String[] supportedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        String expectedUrl = "http://localhost:8080/files/images/test.";

        for (String type : supportedTypes) {
            MockMultipartFile file = new MockMultipartFile(
                "image", "test." + type, "image/" + type, "content".getBytes()
            );

            when(fileStorage.upload(any(MultipartFile.class), anyString()))
                .thenReturn(expectedUrl + type);

            assertDoesNotThrow(() -> fileService.uploadImage(file));
        }

        verify(fileStorage, times(supportedTypes.length))
            .upload(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("支持的文档类型测试")
    void testSupportedDocumentTypes() {
        // 测试支持的文档类型
        String[] supportedTypes = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
        String expectedUrl = "http://localhost:8080/files/documents/test.";

        for (String type : supportedTypes) {
            MockMultipartFile file = new MockMultipartFile(
                "document", "test." + type, "application/" + type, "content".getBytes()
            );

            when(fileStorage.upload(any(MultipartFile.class), anyString()))
                .thenReturn(expectedUrl + type);

            assertDoesNotThrow(() -> fileService.uploadDocument(file));
        }

        verify(fileStorage, times(supportedTypes.length))
            .upload(any(MultipartFile.class), anyString());
    }
}