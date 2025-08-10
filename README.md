# DevWheels 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.7-blue.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> 一个基于 Spring Boot 3 的企业级后端基础框架，提供完整的开发脚手架和最佳实践。

## 📖 项目简介

DevWheels（开发轮子）是一个现代化的 Java 后端基础框架，集成了企业级开发中常用的组件和工具，旨在提高开发效率，减少重复代码，让开发者专注于业务逻辑实现。

### 🎯 核心特性

- 🏗️ **统一架构设计** - 提供通用的基础层架构，标准化开发模式
- 🔐 **权限认证** - 集成 Sa-Token，支持多种认证方式
- 📊 **数据持久化** - MyBatis-Plus 增强，自动填充、逻辑删除等
- 📄 **Excel处理** - 基于 EasyExcel 的完整导入导出解决方案
- 🗂️ **文件管理** - 支持本地存储和云存储的文件上传下载
- ⚡ **异步处理** - 完整的异步任务管理框架
- 📝 **操作日志** - AOP 切面自动记录用户操作
- 🔧 **工具集成** - 丰富的工具类库，涵盖加密、JSON、日期等
- 📚 **API文档** - 集成 Knife4j，自动生成接口文档
- ✅ **单元测试** - 完整的测试用例覆盖

## 🚀 快速开始

### 环境要求

- **Java**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA / Eclipse

### 🔧 安装部署

1. **克隆项目**
```bash
git clone https://github.com/your-username/DevWheels.git
cd DevWheels
```

2. **数据库配置**
```bash
# 创建数据库
CREATE DATABASE dw_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dw_db
    username: your_username
    password: your_password
```

4. **启动项目**
```bash
# 方式一：使用 Maven
./mvnw spring-boot:run

# 方式二：使用 IDE
直接运行 DevWheelsApplication.main()
```

5. **访问应用**
- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/doc.html

## 🏗️ 项目架构

### 📁 目录结构

```
DevWheels/
├── src/main/java/com/dw/
│   ├── common/                    # 通用模块
│   │   ├── annotation/           # 自定义注解
│   │   ├── aspect/              # AOP切面
│   │   ├── async/               # 异步任务
│   │   ├── config/              # 配置类
│   │   ├── controller/          # 基础控制器
│   │   ├── entity/              # 基础实体
│   │   ├── excel/               # Excel处理
│   │   ├── exception/           # 异常处理
│   │   ├── mapper/              # 基础Mapper
│   │   ├── result/              # 统一响应
│   │   ├── service/             # 基础服务
│   │   ├── storage/             # 文件存储
│   │   └── utils/               # 工具类库
│   └── DevWheelsApplication.java # 启动类
├── src/main/resources/
│   ├── application.yml          # 主配置文件
│   └── static/                  # 静态资源
└── src/test/                    # 测试代码
```

### 🎨 核心组件

#### 1. 统一基础层

所有业务模块都应继承以下基础组件：

```java
// 实体基类
public class UserEntity extends DWEntity {
    private String username;
    private String email;
    // getters & setters
}

// Mapper接口
@Mapper
public interface UserMapper extends DWMapper<UserEntity> {
    // 自定义查询方法
}

// Service接口
public interface UserService extends DWService<UserEntity> {
    // 业务方法
}

// Service实现
@Service
public class UserServiceImpl extends DWServiceImpl<UserMapper, UserEntity> 
    implements UserService {
    // 业务实现
}

// Controller
@RestController
@RequestMapping("/users")
public class UserController extends DWController<UserEntity> {
    // REST API
}
```

#### 2. 统一响应体系

```java
// 成功响应
return DWResult.success(data);

// 失败响应  
return DWResult.fail("错误信息");

// 分页响应
return DWResult.success(pageData);
```

#### 3. 操作日志

```java
@RestController
public class UserController {
    
    @OperationLog(value = "用户登录", type = OperationType.LOGIN)
    @PostMapping("/login")
    public DWResult<String> login(@RequestBody LoginRequest request) {
        // 业务逻辑
        return DWResult.success("登录成功");
    }
    
    @OperationLog(value = "创建用户", type = OperationType.INSERT, 
                  recordParams = true, recordResult = true)
    @PostMapping
    public DWResult<UserEntity> createUser(@RequestBody UserEntity user) {
        // 业务逻辑
        return DWResult.success(user);
    }
}
```

#### 4. Excel 处理

```java
@RestController
public class ExcelController {
    
    @Autowired
    private ExcelService excelService;
    
    // 导出Excel
    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) {
        List<UserDto> users = userService.list();
        excelService.exportExcel(response, users, UserDto.class, "用户列表");
    }
    
    // 导入Excel
    @PostMapping("/import")
    public DWResult<ExcelImportResult<UserDto>> importUsers(@RequestParam MultipartFile file) {
        ExcelImportResult<UserDto> result = excelService.importExcel(file, UserDto.class);
        return DWResult.success(result);
    }
}
```

#### 5. 文件上传

```java
@RestController
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @PostMapping("/upload/image")
    public DWResult<String> uploadImage(@RequestParam MultipartFile file) {
        String url = fileService.uploadImage(file);
        return DWResult.success(url);
    }
}
```

#### 6. 异步任务

```java
@Service
public class BusinessService {
    
    @Autowired
    private AsyncTaskService asyncTaskService;
    
    public void processData() {
        // 异步执行任务
        asyncTaskService.executeAsync(() -> {
            // 耗时操作
            System.out.println("异步任务执行中...");
        });
        
        // 异步发送邮件
        asyncTaskService.sendMailAsync("user@example.com", "主题", "内容");
    }
}
```

## 🛠️ 核心功能

### 🔐 权限认证 (Sa-Token)

```java
// 登录
StpUtil.login(userId);

// 权限校验
@SaCheckPermission("user:add")
@PostMapping("/add")
public DWResult<String> addUser() {
    return DWResult.success("添加成功");
}

// 角色校验
@SaCheckRole("admin")
@DeleteMapping("/{id}")
public DWResult<String> deleteUser(@PathVariable Long id) {
    return DWResult.success("删除成功");
}
```

### 📊 数据持久化 (MyBatis-Plus)

```java
// 基础CRUD (继承DWService后自动拥有)
userService.save(user);           // 保存
userService.getById(id);          // 根据ID查询
userService.updateById(user);     // 更新
userService.removeById(id);       // 逻辑删除
userService.page(page, wrapper);  // 分页查询

// 条件构造器
QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
wrapper.eq("status", 1)
       .like("username", "admin")
       .orderByDesc("create_time");
List<UserEntity> users = userService.list(wrapper);
```

### 🧰 工具类库

```java
// ID生成
long id = IdUtils.snowflakeId();      // 雪花算法
String uuid = IdUtils.uuid();         // UUID

// 加密工具
String md5 = EncryptUtils.md5("text");
String sha256 = EncryptUtils.sha256("text");
String[] result = EncryptUtils.hashPassword("password");

// 字符串工具
String camel = StringUtils.toCamelCase("user_name");     // userNaming
String snake = StringUtils.toUnderlineCase("userName"); // user_name
String masked = StringUtils.maskPhone("13812345678");   // 138****5678

// JSON工具
String json = JsonUtils.toJson(object);
User user = JsonUtils.fromJson(json, User.class);

// 日期工具
String formatted = DateUtils.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");
LocalDateTime parsed = DateUtils.parse("2024-01-15 14:30:00", "yyyy-MM-dd HH:mm:ss");
```

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=StringUtilsTest

# 运行特定测试方法
./mvnw test -Dtest=StringUtilsTest#testToCamelCase
```

### 测试覆盖率

项目包含完整的单元测试：

- ✅ 工具类测试 (StringUtils, EncryptUtils, JsonUtils等)
- ✅ 服务层测试 (ExcelService, FileService, AsyncTaskService等)
- ✅ 切面测试 (OperationLogAspect)
- ✅ 异步任务测试 (AsyncTaskManager)
- ✅ 集成测试

## 📚 API文档

项目集成了 Knife4j，启动项目后访问：

- Swagger UI: http://localhost:8080/swagger-ui.html  
- Knife4j: http://localhost:8080/doc.html

## 🔧 配置说明

### 核心配置

```yaml
# application.yml
spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/dw_db
    username: root
    password: root
  
  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 100MB

# MyBatis-Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# Sa-Token配置
sa-token:
  token-name: satoken
  timeout: 2592000
  is-concurrent: true

# 文件存储配置
file:
  storage:
    type: local
    root-path: ./uploads
    url-prefix: /files
```

### 环境配置

支持多环境配置：

- `application.yml` - 主配置
- `application-dev.yml` - 开发环境  
- `application-test.yml` - 测试环境
- `application-prod.yml` - 生产环境

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支: `git checkout -b feature/amazing-feature`
3. 提交更改: `git commit -m 'Add some amazing feature'`
4. 推送分支: `git push origin feature/amazing-feature`
5. 提交 Pull Request

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用统一的代码格式化配置
- 编写完整的单元测试
- 添加必要的注释和文档

## 📄 更新日志

### v1.0.0 (2024-01-15)

**新特性:**
- ✨ 基础架构搭建完成
- ✨ 集成 MyBatis-Plus 数据持久化
- ✨ 集成 Sa-Token 权限认证
- ✨ Excel 导入导出功能
- ✨ 文件上传下载管理
- ✨ 异步任务处理框架
- ✨ 操作日志记录
- ✨ 丰富的工具类库
- ✨ 完整的单元测试覆盖
- ✨ API 文档自动生成

**技术栈:**
- Spring Boot 3.5.4
- Java 17
- MyBatis-Plus 3.5.7
- Sa-Token 1.44.0
- EasyExcel 3.3.4
- Knife4j 4.4.0

## 📞 技术支持

- 📧 邮箱: [your-email@example.com]
- 💬 Issue: [GitHub Issues](https://github.com/your-username/DevWheels/issues)
- 📖 文档: [项目Wiki](https://github.com/your-username/DevWheels/wiki)

## 📋 TODO

- [ ] 集成 Redis 缓存
- [ ] 添加消息队列支持
- [ ] 集成 Docker 容器化部署  
- [ ] 添加分布式锁
- [ ] 支持多数据源
- [ ] 集成监控告警
- [ ] 添加定时任务管理
- [ ] 支持多租户

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源协议。

---

⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！

## 🙏 致谢

感谢以下开源项目和社区的支持：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Sa-Token](https://sa-token.cc/)
- [EasyExcel](https://github.com/alibaba/easyexcel)
- [Knife4j](https://doc.xiaominfo.com/)

---

<div align="center">
  
**DevWheels - 让开发更简单** 🚀

[⬆ 回到顶部](#devwheels-)

</div>