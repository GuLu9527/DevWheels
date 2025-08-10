package com.dw;

import com.dw.common.aspect.OperationLogAspectTest;
import com.dw.common.async.AsyncTaskManagerTest;
import com.dw.common.service.AsyncTaskServiceTest;
import com.dw.common.service.ExcelServiceTest;
import com.dw.common.service.FileServiceTest;
import com.dw.common.storage.LocalFileStorageTest;
import com.dw.common.utils.*;
import com.dw.devwheels.DevWheelsApplicationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * DevWheels 完整测试套件
 * 包含所有功能模块的测试类
 */
@Suite
@DisplayName("DevWheels完整测试套件")
@SelectClasses({
    // 应用程序基础测试
    DevWheelsApplicationTests.class,
    
    // ID生成器测试
    IdUtilsTest.class,
    SnowflakeIdGeneratorTest.class,
    
    // 工具类库测试
    StringUtilsTest.class,
    DateUtilsTest.class,
    EncryptUtilsTest.class,
    JsonUtilsTest.class,
    
    // Excel处理测试
    ExcelServiceTest.class,
    ExcelUtilsTest.class,
    
    // 文件管理测试
    FileServiceTest.class,
    LocalFileStorageTest.class,
    
    // 异步任务测试
    AsyncTaskServiceTest.class,
    AsyncTaskManagerTest.class,
    
    // 操作日志测试
    OperationLogAspectTest.class
})
public class DevWheelsTestSuite {
    // 测试套件类，用于批量执行所有测试
}