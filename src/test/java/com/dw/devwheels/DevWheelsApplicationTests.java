package com.dw.devwheels;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DevWheelsApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring应用上下文是否能正常加载
        // 如果能运行到这里说明所有的Bean都能正常创建和注入
        // 使用MySQL测试数据库，确保与生产环境数据库类型一致
    }

}
