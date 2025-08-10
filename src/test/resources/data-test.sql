-- 测试数据初始化脚本

-- 插入测试用户数据
INSERT INTO test_user (name, age, email) VALUES 
('张三', 25, 'zhangsan@test.com'),
('李四', 30, 'lisi@test.com'),
('王五', 28, 'wangwu@test.com'),
('赵六', 35, 'zhaoliu@test.com'),
('孙七', 22, 'sunqi@test.com');

-- 插入测试产品数据
INSERT INTO test_product (name, price, stock) VALUES 
('笔记本电脑', 5999.99, 50),
('无线鼠标', 99.99, 200),
('机械键盘', 299.99, 150),
('显示器', 1999.99, 80),
('音箱', 399.99, 120),
('摄像头', 199.99, 100),
('耳机', 299.99, 180),
('手机', 2999.99, 60),
('平板电脑', 1799.99, 40),
('充电器', 79.99, 300);

-- 插入测试操作日志数据
INSERT INTO sys_operation_log (user_id, username, operation, operation_type, method, uri, ip, execution_time, success) VALUES 
(1, 'testuser1', '用户登录', '登录', 'POST', '/api/login', '192.168.1.100', 150, true),
(1, 'testuser1', '查询用户列表', '查询', 'GET', '/api/users', '192.168.1.100', 80, true),
(2, 'testuser2', '新增产品', '新增', 'POST', '/api/products', '192.168.1.101', 200, true),
(2, 'testuser2', '更新产品信息', '修改', 'PUT', '/api/products/1', '192.168.1.101', 120, true),
(1, 'testuser1', '删除产品', '删除', 'DELETE', '/api/products/2', '192.168.1.100', 90, true);