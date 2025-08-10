package com.dw.common;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 集成测试基类
 * 用于需要Spring容器的测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@DisplayName("集成测试基类")
public abstract class IntegrationTest extends BaseTest {
    
    /**
     * 清理测试数据
     * 子类可以重写此方法来清理特定的测试数据
     */
    protected void cleanupTestData() {
        // 默认由@Transactional回滚处理
    }
    
    /**
     * 准备测试数据
     * 子类可以重写此方法来准备特定的测试数据
     */
    protected void prepareTestData() {
        // 子类实现
    }
}