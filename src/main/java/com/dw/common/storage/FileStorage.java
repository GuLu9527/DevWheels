package com.dw.common.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储策略接口
 */
public interface FileStorage {
    
    /**
     * 上传文件
     * @param file 文件
     * @param path 存储路径
     * @return 文件访问URL
     */
    String upload(MultipartFile file, String path);
    
    /**
     * 上传文件（流）
     * @param inputStream 文件流
     * @param path 存储路径
     * @param contentType 文件类型
     * @return 文件访问URL
     */
    String upload(InputStream inputStream, String path, String contentType);
    
    /**
     * 删除文件
     * @param path 文件路径
     * @return 是否删除成功
     */
    boolean delete(String path);
    
    /**
     * 获取文件访问URL
     * @param path 文件路径
     * @return 访问URL
     */
    String getUrl(String path);
    
    /**
     * 检查文件是否存在
     * @param path 文件路径
     * @return 是否存在
     */
    boolean exists(String path);
}