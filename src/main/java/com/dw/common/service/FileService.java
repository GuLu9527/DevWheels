package com.dw.common.service;

import com.dw.common.storage.FileStorage;
import com.dw.common.utils.DateUtils;
import com.dw.common.utils.IdUtils;
import com.dw.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 文件服务
 */
@Slf4j
@Service
public class FileService {
    
    @Autowired
    private FileStorage fileStorage;
    
    // 允许上传的图片类型
    private static final List<String> IMAGE_TYPES = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );
    
    // 允许上传的文档类型
    private static final List<String> DOCUMENT_TYPES = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );
    
    // 最大文件大小（10MB）
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        return uploadFile(file, "images", IMAGE_TYPES);
    }
    
    /**
     * 上传文档
     */
    public String uploadDocument(MultipartFile file) {
        return uploadFile(file, "documents", DOCUMENT_TYPES);
    }
    
    /**
     * 通用文件上传
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "files", null);
    }
    
    /**
     * 文件上传核心方法
     */
    private String uploadFile(MultipartFile file, String category, List<String> allowedTypes) {
        // 基础校验
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 文件大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        
        // 获取文件信息
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (StringUtils.isEmpty(fileExtension)) {
            throw new IllegalArgumentException("文件扩展名不能为空");
        }
        
        // 文件类型校验
        if (allowedTypes != null && !allowedTypes.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }
        
        // 生成存储路径
        String storagePath = generateStoragePath(category, fileExtension);
        
        // 上传文件
        String url = fileStorage.upload(file, storagePath);
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("文件上传失败");
        }
        
        log.info("文件上传成功: {} -> {}", originalFilename, url);
        return url;
    }
    
    /**
     * 流式上传文件
     */
    public String uploadFile(InputStream inputStream, String filename, String contentType) {
        if (inputStream == null || StringUtils.isEmpty(filename)) {
            throw new IllegalArgumentException("输入流和文件名不能为空");
        }
        
        String fileExtension = getFileExtension(filename);
        String storagePath = generateStoragePath("files", fileExtension);
        
        String url = fileStorage.upload(inputStream, storagePath, contentType);
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("文件上传失败");
        }
        
        return url;
    }
    
    /**
     * 删除文件
     */
    public boolean deleteFile(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        
        // 从URL中提取文件路径
        String path = extractPathFromUrl(url);
        return fileStorage.delete(path);
    }
    
    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        
        String path = extractPathFromUrl(url);
        return fileStorage.exists(path);
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }
    
    /**
     * 生成存储路径
     */
    private String generateStoragePath(String category, String extension) {
        String dateStr = DateUtils.format(LocalDate.now(), "yyyy/MM/dd");
        String filename = IdUtils.uuid() + "." + extension.toLowerCase();
        return String.format("%s/%s/%s", category, dateStr, filename);
    }
    
    /**
     * 从URL中提取文件路径
     */
    private String extractPathFromUrl(String url) {
        // 这里需要根据具体的URL格式来提取路径
        // 简单实现：假设URL格式为 http://domain/files/xxx
        int index = url.lastIndexOf("/files/");
        return index > 0 ? url.substring(index + 1) : url;
    }
}