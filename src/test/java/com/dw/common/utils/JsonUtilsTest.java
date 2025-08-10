package com.dw.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@DisplayName("JSON工具类测试")
public class JsonUtilsTest {

    // 测试用的简单POJO类
    static class TestUser {
        private String name;
        private int age;
        private LocalDateTime createTime;
        private List<String> hobbies;
        
        public TestUser() {}
        
        public TestUser(String name, int age, LocalDateTime createTime, List<String> hobbies) {
            this.name = name;
            this.age = age;
            this.createTime = createTime;
            this.hobbies = hobbies;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public List<String> getHobbies() { return hobbies; }
        public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestUser testUser = (TestUser) obj;
            return age == testUser.age &&
                   name.equals(testUser.name) &&
                   createTime.equals(testUser.createTime) &&
                   hobbies.equals(testUser.hobbies);
        }
    }

    @Test
    @DisplayName("对象转JSON字符串测试")
    void testToJson() {
        // 简单对象测试
        TestUser user = new TestUser("张三", 25, 
            LocalDateTime.of(2024, 1, 15, 14, 30, 0),
            Arrays.asList("reading", "coding", "music"));
        
        String json = JsonUtils.toJson(user);
        assertNotNull(json);
        assertTrue(json.contains("张三"));
        assertTrue(json.contains("25"));
        assertTrue(json.contains("reading"));
        assertTrue(json.contains("coding"));
        assertTrue(json.contains("music"));
        
        System.out.println("用户对象JSON: " + json);
        
        // Map测试
        Map<String, Object> map = new HashMap<>();
        map.put("name", "李四");
        map.put("age", 30);
        map.put("active", true);
        
        String mapJson = JsonUtils.toJson(map);
        assertNotNull(mapJson);
        assertTrue(mapJson.contains("李四"));
        assertTrue(mapJson.contains("30"));
        assertTrue(mapJson.contains("true"));
        
        // List测试
        List<String> list = Arrays.asList("apple", "banana", "orange");
        String listJson = JsonUtils.toJson(list);
        assertNotNull(listJson);
        assertTrue(listJson.contains("apple"));
        assertTrue(listJson.contains("banana"));
        assertTrue(listJson.contains("orange"));
        
        // null测试
        assertNull(JsonUtils.toJson(null));
    }
    
    @Test
    @DisplayName("JSON字符串转对象测试")
    void testFromJson() {
        // 准备测试数据
        TestUser originalUser = new TestUser("王五", 28,
            LocalDateTime.of(2024, 1, 15, 10, 20, 30),
            Arrays.asList("swimming", "running"));
        
        String json = JsonUtils.toJson(originalUser);
        
        // JSON转对象
        TestUser parsedUser = JsonUtils.fromJson(json, TestUser.class);
        assertNotNull(parsedUser);
        assertEquals(originalUser.getName(), parsedUser.getName());
        assertEquals(originalUser.getAge(), parsedUser.getAge());
        assertEquals(originalUser.getCreateTime(), parsedUser.getCreateTime());
        assertEquals(originalUser.getHobbies(), parsedUser.getHobbies());
        
        // Map测试
        String mapJson = "{\"name\":\"赵六\",\"age\":35,\"active\":false}";
        Map<String, Object> map = JsonUtils.fromJson(mapJson, Map.class);
        assertNotNull(map);
        assertEquals("赵六", map.get("name"));
        assertEquals(35, ((Number) map.get("age")).intValue());
        assertEquals(false, map.get("active"));
        
        // null和空字符串测试
        assertNull(JsonUtils.fromJson(null, TestUser.class));
        assertNull(JsonUtils.fromJson("", TestUser.class));
        assertNull(JsonUtils.fromJson("   ", TestUser.class));
    }
    
    @Test
    @DisplayName("格式化JSON测试")
    void testPrettyJson() {
        Map<String, Object> complexObject = new HashMap<>();
        complexObject.put("name", "测试用户");
        complexObject.put("age", 25);
        complexObject.put("address", new HashMap<String, String>() {{
            put("city", "北京");
            put("district", "朝阳区");
        }});
        complexObject.put("tags", Arrays.asList("developer", "java", "spring"));
        
        String prettyJson = JsonUtils.prettyJson(complexObject);
        assertNotNull(prettyJson);
        
        // 格式化的JSON应该包含换行符和缩进
        assertTrue(prettyJson.contains("\n"));
        assertTrue(prettyJson.contains("  ")); // 缩进空格
        
        System.out.println("格式化JSON:");
        System.out.println(prettyJson);
        
        // 对比普通JSON
        String normalJson = JsonUtils.toJson(complexObject);
        assertTrue(prettyJson.length() > normalJson.length()); // 格式化后长度应该更长
        
        // null测试
        assertNull(JsonUtils.prettyJson(null));
    }
    
    @Test
    @DisplayName("复杂数据类型测试")
    void testComplexDataTypes() {
        // 嵌套对象测试
        Map<String, Object> nestedObject = new HashMap<>();
        nestedObject.put("user", new TestUser("嵌套用户", 30,
            LocalDateTime.of(2024, 1, 20, 15, 45, 0),
            Arrays.asList("nested", "test")));
        nestedObject.put("metadata", new HashMap<String, Object>() {{
            put("version", "1.0");
            put("timestamp", LocalDateTime.now());
            put("numbers", Arrays.asList(1, 2, 3, 4, 5));
        }});
        
        String json = JsonUtils.toJson(nestedObject);
        assertNotNull(json);
        
        // 反序列化
        Map<String, Object> parsed = JsonUtils.fromJson(json, Map.class);
        assertNotNull(parsed);
        assertTrue(parsed.containsKey("user"));
        assertTrue(parsed.containsKey("metadata"));
    }
    
    @Test
    @DisplayName("边界情况测试")
    void testEdgeCases() {
        // 空对象测试
        Map<String, Object> emptyMap = new HashMap<>();
        String emptyJson = JsonUtils.toJson(emptyMap);
        assertEquals("{}", emptyJson);
        
        // 空数组测试
        List<String> emptyList = Arrays.asList();
        String emptyListJson = JsonUtils.toJson(emptyList);
        assertEquals("[]", emptyListJson);
        
        // 特殊字符测试
        Map<String, String> specialChars = new HashMap<>();
        specialChars.put("special", "特殊字符\"\\/'测试\n\r\t");
        String specialJson = JsonUtils.toJson(specialChars);
        assertNotNull(specialJson);
        
        Map<String, String> parsedSpecial = JsonUtils.fromJson(specialJson, Map.class);
        assertEquals("特殊字符\"\\/'测试\n\r\t", parsedSpecial.get("special"));
    }
    
    @Test
    @DisplayName("时间格式处理测试")
    void testDateTimeHandling() {
        TestUser user = new TestUser("时间测试", 25,
            LocalDateTime.of(2024, 1, 15, 14, 30, 45),
            Arrays.asList("time", "test"));
        
        String json = JsonUtils.toJson(user);
        assertNotNull(json);
        
        // 验证包含时间信息
        assertTrue(json.contains("2024"));
        assertTrue(json.contains("14"));
        assertTrue(json.contains("30"));
        assertTrue(json.contains("45"));
        
        // 反序列化验证时间正确性
        TestUser parsed = JsonUtils.fromJson(json, TestUser.class);
        assertNotNull(parsed);
        assertEquals(2024, parsed.getCreateTime().getYear());
        assertEquals(1, parsed.getCreateTime().getMonthValue());
        assertEquals(15, parsed.getCreateTime().getDayOfMonth());
        assertEquals(14, parsed.getCreateTime().getHour());
        assertEquals(30, parsed.getCreateTime().getMinute());
        assertEquals(45, parsed.getCreateTime().getSecond());
    }
    
    @Test
    @DisplayName("错误处理测试")
    void testErrorHandling() {
        // 无效JSON字符串
        String invalidJson = "{ invalid json string";
        TestUser result = JsonUtils.fromJson(invalidJson, TestUser.class);
        assertNull(result); // 应该返回null而不是抛出异常
        
        // 类型不匹配的JSON
        String wrongTypeJson = "\"this is a string\"";
        TestUser wrongResult = JsonUtils.fromJson(wrongTypeJson, TestUser.class);
        assertNull(wrongResult);
        
        // 循环引用对象（这个测试可能需要特殊的对象结构）
        Map<String, Object> selfRef = new HashMap<>();
        selfRef.put("self", selfRef); // 创建循环引用
        
        // 应该能处理而不崩溃
        String selfRefJson = JsonUtils.toJson(selfRef);
        // 由于循环引用，可能返回null或处理后的结果
        // 这里主要测试不会抛出异常
        assertDoesNotThrow(() -> JsonUtils.toJson(selfRef));
    }
    
    @Test
    @DisplayName("性能测试")
    void testPerformance() {
        TestUser user = new TestUser("性能测试", 25,
            LocalDateTime.now(),
            Arrays.asList("performance", "test", "json"));
        
        int iterations = 10000;
        
        // 序列化性能测试
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            JsonUtils.toJson(user);
        }
        long serializeTime = System.currentTimeMillis() - startTime;
        
        // 反序列化性能测试
        String json = JsonUtils.toJson(user);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            JsonUtils.fromJson(json, TestUser.class);
        }
        long deserializeTime = System.currentTimeMillis() - startTime;
        
        System.out.println("JSON序列化 " + iterations + " 次耗时: " + serializeTime + "ms");
        System.out.println("JSON反序列化 " + iterations + " 次耗时: " + deserializeTime + "ms");
        
        // 性能要求
        assertTrue(serializeTime < 3000, "JSON序列化性能应该在3秒内完成");
        assertTrue(deserializeTime < 3000, "JSON反序列化性能应该在3秒内完成");
    }
}