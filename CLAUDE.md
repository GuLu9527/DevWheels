# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

DevWheels 是一个基础轮子项目，旨在为后续项目开发提供通用的代码基础和架构模板。基于 Spring Boot 3.5.4 + Java 17，集成了常用的开发组件和最佳实践。

## 开发命令

### 项目启动
```bash
./mvnw spring-boot:run
```

### 构建项目
```bash
./mvnw clean package
```

### 运行测试
```bash
./mvnw test
```

## 核心架构

### 通用基础层设计
项目采用统一的基础层架构，所有业务模块都应继承以下基础组件：

- **DWEntity**: 通用实体基类，包含 id、createTime、updateTime、isDeleted 字段，支持逻辑删除和自动时间填充
- **DWMapper**: 通用 Mapper 接口，继承 MyBatis-Plus BaseMapper
- **DWService**: 通用 Service 接口，定义标准的 CRUD 操作
- **DWServiceImpl**: 通用 Service 实现类，提供带事务的基础操作
- **DWController**: 通用 Controller 基类，提供标准的 REST API 接口

### 统一响应体系
- **DWResult<T>**: 统一响应包装类，包含 code、msg、data 字段
- 成功响应: `DWResult.success(data)` 或 `DWResult.success()`
- 失败响应: `DWResult.fail(message)` 或 `DWResult.fail(code, message)`

### 异常处理体系
- **GlobalExceptionHandler**: 全局异常处理器，统一处理各种异常
- **BusinessException**: 自定义业务异常类
- 支持参数校验异常、约束校验异常、业务异常等的统一处理

### 配置体系
- **MyBatis-Plus**: 已配置分页插件、逻辑删除、自动时间填充
- **Sa-Token**: 权限认证框架，配置了 token 管理
- **Knife4j**: API 文档工具，访问地址 `/doc.html`

## 开发规范

### 新增业务模块步骤
1. 创建实体类继承 `DWEntity`
2. 创建 Mapper 接口继承 `DWMapper<T>`
3. 创建 Service 接口继承 `DWService<T>`
4. 创建 ServiceImpl 继承 `DWServiceImpl<M,T>` 并实现 Service 接口
5. 创建 Controller 继承 `DWController<T>`

### 数据库字段命名
- 使用下划线命名法，如 `user_name`
- 配置已支持自动映射到驼峰命名的 Java 字段，如 `userName`

### 事务管理
- 基础 CRUD 操作已在 `DWServiceImpl` 中配置 `@Transactional(rollbackFor = Exception.class)`
- 新增业务方法需要事务时，使用相同的注解配置

### 参数校验
- 在 Controller 方法参数上使用 `@Validated`
- 在实体类字段上使用 JSR303 校验注解
- 全局异常处理器会自动处理校验失败的情况

## 核心功能模块

### ID生成器
- **IdUtils**: 提供雪花算法、UUID等多种ID生成策略
- **SnowflakeIdGenerator**: 高性能分布式ID生成器

### 操作日志
- **@OperationLog**: 操作日志注解，自动记录用户操作
- **OperationLogAspect**: AOP切面，支持IP获取和执行时间统计
- **SysOperationLog**: 操作日志实体，记录详细的审计信息

### 工具类库
- **JsonUtils**: JSON序列化/反序列化工具
- **StringUtils**: 字符串处理工具，支持驼峰转换、脱敏等
- **DateUtils**: 日期时间处理工具，基于Java 8时间API
- **EncryptUtils**: 加密工具，支持MD5、SHA256、Base64等

### 文件管理
- **FileStorage**: 文件存储策略接口，支持本地存储和云存储扩展
- **FileService**: 文件上传下载服务，支持图片、文档分类管理
- **FileStorageConfig**: 文件存储配置，支持多种存储方式切换

### Excel处理
- **ExcelUtils**: Excel导入导出工具，基于EasyExcel
- **ExcelService**: Excel服务，支持大文件处理和数据校验
- **ExcelImportResult**: 导入结果封装，提供详细的错误信息

### 异步任务
- **AsyncTaskService**: 异步任务服务，支持邮件发送、文件处理等
- **AsyncTaskManager**: 异步任务管理器，支持链式、并行、超时等执行模式
- **AsyncConfig**: 异步线程池配置，针对不同场景优化

## 使用示例

### ID生成器使用
```java
// 雪花算法ID
long id = IdUtils.snowflakeId();
// UUID
String uuid = IdUtils.uuid();
```

### 操作日志使用
```java
@OperationLog(value = "用户登录", type = OperationType.LOGIN, recordParams = true)
public DWResult<String> login(@RequestBody LoginRequest request) {
    // 业务逻辑
}
```

### 文件上传使用
```java
@Autowired
private FileService fileService;

// 上传图片
String imageUrl = fileService.uploadImage(file);
// 上传文档
String docUrl = fileService.uploadDocument(file);
```

### Excel操作使用
```java
@Autowired
private ExcelService excelService;

// 导出Excel
excelService.exportExcel(response, dataList, UserDto.class, "用户列表");
// 导入Excel
ExcelImportResult<UserDto> result = excelService.importExcel(file, UserDto.class);
```

### 异步任务使用
```java
@Autowired
private AsyncTaskService asyncTaskService;

// 简单异步任务
asyncTaskService.executeAsync(() -> {
    // 异步处理逻辑
});

// 异步发送邮件
asyncTaskService.sendMailAsync(to, subject, content);
```

## 技术栈
- Spring Boot 3.5.4
- Java 17
- MyBatis-Plus 3.5.7
- Sa-Token 1.44.0 (权限认证)
- Knife4j 4.4.0 (API 文档)
- EasyExcel 3.3.4 (Excel处理)
- MySQL Connector
- Lombok
- Spring Validation