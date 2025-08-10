-- 测试数据库初始化脚本
-- 创建操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(100) COMMENT '操作用户名',
    operation VARCHAR(500) COMMENT '操作描述',
    operation_type VARCHAR(50) COMMENT '操作类型',
    method VARCHAR(10) COMMENT '请求方法',
    uri VARCHAR(500) COMMENT '请求URI',
    ip VARCHAR(100) COMMENT '请求IP',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '返回结果',
    execution_time BIGINT COMMENT '执行时间（毫秒）',
    success BOOLEAN COMMENT '是否成功',
    error_msg TEXT COMMENT '异常信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记'
) COMMENT = '系统操作日志表';

-- 创建测试用户表
CREATE TABLE IF NOT EXISTS test_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '用户名',
    age INT COMMENT '年龄',
    email VARCHAR(200) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记'
) COMMENT = '测试用户表';

-- 创建测试产品表
CREATE TABLE IF NOT EXISTS test_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '产品名称',
    price DECIMAL(10,2) COMMENT '价格',
    stock INT COMMENT '库存',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记'
) COMMENT = '测试产品表';