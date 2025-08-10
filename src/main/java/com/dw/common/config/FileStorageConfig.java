package com.dw.common.config;

import com.dw.common.storage.FileStorage;
import com.dw.common.storage.LocalFileStorage;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件存储配置
 */
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig implements WebMvcConfigurer {
    
    private String type = "local";
    private String rootPath = "./uploads";
    private String urlPrefix = "/files";
    
    @Bean
    public FileStorage fileStorage() {
        switch (type.toLowerCase()) {
            case "local":
            default:
                return new LocalFileStorage(rootPath, urlPrefix);
                // 其他存储类型可在此扩展
                // case "oss": return new OssFileStorage();
                // case "cos": return new CosFileStorage();
        }
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问映射
        if ("local".equalsIgnoreCase(type)) {
            registry.addResourceHandler(urlPrefix + "/**")
                    .addResourceLocations("file:" + rootPath + "/");
        }
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRootPath() {
        return rootPath;
    }
    
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    
    public String getUrlPrefix() {
        return urlPrefix;
    }
    
    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}