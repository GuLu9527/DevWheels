package com.dw.common.storage;

import com.dw.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件存储实现
 */
@Slf4j
public class LocalFileStorage implements FileStorage {
    
    private final String rootPath;
    private final String urlPrefix;
    
    public LocalFileStorage(String rootPath, String urlPrefix) {
        this.rootPath = rootPath;
        this.urlPrefix = urlPrefix;
        
        // 确保根目录存在
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }
    
    @Override
    public String upload(MultipartFile file, String path) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            return upload(file.getInputStream(), path, file.getContentType());
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        if (inputStream == null || StringUtils.isEmpty(path)) {
            return null;
        }
        
        try {
            Path filePath = Paths.get(rootPath, path);
            
            // 创建父目录
            Files.createDirectories(filePath.getParent());
            
            // 保存文件
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("文件上传成功: {}", filePath);
            return getUrl(path);
            
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean delete(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        
        try {
            Path filePath = Paths.get(rootPath, path);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("文件删除成功: {}", filePath);
            }
            return deleted;
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getUrl(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return urlPrefix + (path.startsWith("/") ? path : "/" + path);
    }
    
    @Override
    public boolean exists(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        Path filePath = Paths.get(rootPath, path);
        return Files.exists(filePath);
    }
}