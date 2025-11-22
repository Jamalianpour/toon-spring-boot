package io.github.jamalianpour.toon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamalianpour.toon.annotation.ToonField;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import io.github.jamalianpour.toon.core.ToonConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Real-world scenario tests demonstrating practical TOON usage.
 */
class RealWorldScenarioTest {

    private ToonConverter converter;
    private ObjectMapper jsonMapper;

    @BeforeEach
    void setUp() {
        converter = new ToonConverter();
        jsonMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Scenario: LLM Chat History for RAG System")
    void testLLMChatHistory() throws Exception {
        // Create chat history for a RAG system
        ChatSession session = new ChatSession();
        session.setSessionId("sess-123456");
        session.setUserId("user-789");
        session.setModel("gpt-4");

        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage("user", "What is TOON format?", "2024-01-15T10:00:00", 15),
                new ChatMessage("assistant", "TOON is Token-Oriented Object Notation...", "2024-01-15T10:00:05", 45),
                new ChatMessage("user", "How does it compare to JSON?", "2024-01-15T10:00:30", 12),
                new ChatMessage("assistant", "TOON reduces tokens by 30-60% compared to JSON...", "2024-01-15T10:00:35", 52)
        );
        session.setMessages(messages);

        String toon = converter.toToon(session);
        String json = jsonMapper.writeValueAsString(session);

        System.out.println("=== LLM Chat History ===");
        System.out.println("TOON Output:");
        System.out.println(toon);
        System.out.println("\nSize comparison:");
        System.out.println("TOON: " + toon.length() + " chars");
        System.out.println("JSON: " + json.length() + " chars");
        System.out.println("Reduction: " + calculateReduction(json.length(), toon.length()) + "%");

        assertThat(toon).contains("messages[4]{role,content,timestamp,tokenCount}:");
        assertThat(toon.length()).isLessThan(json.length());
    }

    @Test
    @DisplayName("Scenario: IoT Sensor Data Stream")
    void testIoTSensorData() throws Exception {
        // Create IoT sensor readings
        SensorDevice device = new SensorDevice();
        device.setDeviceId("SENSOR-001");
        device.setLocation("Building A, Floor 3");
        device.setType("Environmental");

        List<SensorReading> readings = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            readings.add(new SensorReading(
                    "2024-01-15T10:" + String.format("%02d", i) + ":00",
                    22.5 + (i * 0.1),
                    45.0 + (i * 0.5),
                    1013.25 + (i * 0.01),
                    i % 5 == 0
            ));
        }
        device.setReadings(readings);

        String toon = converter.toToon(device);
        String json = jsonMapper.writeValueAsString(device);

        System.out.println("\n=== IoT Sensor Data ===");
        System.out.println("First few lines of TOON:");
        System.out.println(toon.substring(0, Math.min(500, toon.length())) + "...");
        System.out.println("\nSize comparison:");
        System.out.println("TOON: " + toon.length() + " chars");
        System.out.println("JSON: " + json.length() + " chars");
        System.out.println("Reduction: " + calculateReduction(json.length(), toon.length()) + "%");

        assertThat(toon).contains("readings[20]{timestamp,temperature,humidity,pressure,alert}:");
        assertThat(calculateReduction(json.length(), toon.length())).isGreaterThan(40);
    }

    @Test
    @DisplayName("Scenario: Machine Learning Dataset")
    void testMLDataset() throws Exception {
        // Create ML training dataset
        MLDataset dataset = new MLDataset();
        dataset.setName("customer_churn_prediction");
        dataset.setVersion("2.0");

        List<DataPoint> trainingData = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            trainingData.add(new DataPoint(
                    i,
                    Arrays.asList(
                            25.0 + (i % 40),  // age
                            (i % 3) * 50000.0 + 30000,  // income
                            (i % 5) + 1.0,  // satisfaction score
                            (i % 10) * 10.0  // usage hours
                    ),
                    i % 3 == 0 ? 1 : 0  // churned
            ));
        }
        dataset.setData(trainingData);

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("features", Arrays.asList("age", "income", "satisfaction", "usage"));
        metadata.put("targetVariable", "churned");
        metadata.put("samplesCount", 50);
        dataset.setMetadata(metadata);

        String toon = converter.toToon(dataset);
        String json = jsonMapper.writeValueAsString(dataset);

        System.out.println("\n=== ML Dataset ===");
        System.out.println("Dataset structure in TOON:");
        String preview = toon.substring(0, Math.min(400, toon.length()));
        System.out.println(preview + "...");
        System.out.println("\nSize comparison:");
        System.out.println("TOON: " + toon.length() + " chars");
        System.out.println("JSON: " + json.length() + " chars");
        System.out.println("Reduction: " + calculateReduction(json.length(), toon.length()) + "%");

        assertThat(toon).contains("name: customer_churn_prediction");
        assertThat(toon).contains("data[50]:");
        assertThat(toon).contains("- id: 1");
    }

    @Test
    @DisplayName("Scenario: Microservices Configuration")
    void testMicroservicesConfig() throws Exception {
        // Create microservices configuration
        ServiceMesh mesh = new ServiceMesh();
        mesh.setEnvironment("production");
        mesh.setRegion("us-west-2");

        List<ServiceConfig> services = Arrays.asList(
                new ServiceConfig("auth-service", "v2.1.0", 3, 512, 1024, Arrays.asList("POST /login", "POST /logout", "GET /verify")),
                new ServiceConfig("user-service", "v3.0.1", 5, 1024, 2048, Arrays.asList("GET /users", "POST /users", "PUT /users/{id}")),
                new ServiceConfig("payment-service", "v1.5.2", 2, 768, 1536, Arrays.asList("POST /charge", "GET /transactions")),
                new ServiceConfig("notification-service", "v2.0.0", 4, 256, 512, Arrays.asList("POST /email", "POST /sms", "POST /push"))
        );
        mesh.setServices(services);

        String toon = converter.toToon(mesh);
        String json = jsonMapper.writeValueAsString(mesh);

        System.out.println("\n=== Microservices Configuration ===");
        System.out.println("TOON Output:");
        System.out.println(toon);
        System.out.println("\nSize comparison:");
        System.out.println("TOON: " + toon.length() + " chars");
        System.out.println("JSON: " + json.length() + " chars");
        System.out.println("Reduction: " + calculateReduction(json.length(), toon.length()) + "%");

        assertThat(toon).contains("services[4]:");
        assertThat(toon).contains("- name: auth-service");
    }

    @Test
    @DisplayName("Scenario: Analytics Dashboard Data")
    void testAnalyticsDashboard() throws Exception {
        // Create analytics dashboard data
        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardId("DASH-001");
        dashboard.setTitle("Sales Analytics Q1 2024");

        List<Metric> metrics = Arrays.asList(
                new Metric("revenue", 1250000.50, 1100000.00, "+13.6%"),
                new Metric("orders", 3542, 3100, "+14.3%"),
                new Metric("customers", 892, 750, "+18.9%"),
                new Metric("avg_order_value", 353.15, 354.84, "-0.5%")
        );
        dashboard.setMetrics(metrics);

        List<ChartData> chartData = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar"};
        for (String month : months) {
            chartData.add(new ChartData(
                    month,
                    Arrays.asList(
                            random(300000, 500000),  // revenue
                            random(1000, 1500),       // orders
                            random(250, 350)          // new customers
                    )
            ));
        }
        dashboard.setChartData(chartData);

        String toon = converter.toToon(dashboard);

        System.out.println("\n=== Analytics Dashboard ===");
        System.out.println("TOON Output:");
        System.out.println(toon);

        assertThat(toon).contains("metrics[4]{name,current,previous,change}:");
        assertThat(toon).contains("chartData[3]:");
    }

    @Test
    @DisplayName("Scenario: E-learning Course Structure")
    void testElearningCourse() throws Exception {
        // Create e-learning course structure
        Course course = new Course();
        course.setCourseId("CS-101");
        course.setTitle("Introduction to TOON Format");
        course.setInstructor("Dr. Jane Smith");

        List<Module> modules = Arrays.asList(
                createModule(1, "Getting Started", Arrays.asList(
                        new Lesson(1, "What is TOON?", 15, true),
                        new Lesson(2, "Why Use TOON?", 20, true),
                        new Lesson(3, "TOON vs JSON", 25, false)
                )),
                createModule(2, "Basic Syntax", Arrays.asList(
                        new Lesson(4, "Primitives and Strings", 30, false),
                        new Lesson(5, "Arrays and Collections", 35, false),
                        new Lesson(6, "Objects and Nesting", 40, false)
                )),
                createModule(3, "Advanced Topics", Arrays.asList(
                        new Lesson(7, "Annotations", 45, false),
                        new Lesson(8, "Configuration", 30, false)
                ))
        );
        course.setModules(modules);

        String toon = converter.toToon(course);
        String json = jsonMapper.writeValueAsString(course);

        System.out.println("\n=== E-learning Course ===");
        System.out.println("TOON Output:");
        System.out.println(toon);
        System.out.println("\nSize comparison:");
        System.out.println("TOON: " + toon.length() + " chars");
        System.out.println("JSON: " + json.length() + " chars");
        System.out.println("Reduction: " + calculateReduction(json.length(), toon.length()) + "%");

        assertThat(toon).contains("modules[3]:");
        assertThat(toon).contains("lessons[");
    }

    @Test
    @DisplayName("Scenario: Social Media Posts Feed")
    void testSocialMediaFeed() throws Exception {
        // Create social media feed
        List<Post> posts = Arrays.asList(
                new Post(1001, "user123", "Just learned about TOON format! 🚀", "2024-01-15T10:00:00", 42, 5, Arrays.asList("#toon", "#coding", "#optimization")),
                new Post(1002, "dev_alice", "TOON reduced my LLM costs by 45%! Here's how...", "2024-01-15T09:30:00", 156, 23, Arrays.asList("#llm", "#toon", "#costsaving")),
                new Post(1003, "tech_bob", "Comparing JSON vs TOON for production APIs", "2024-01-15T08:45:00", 89, 12, Arrays.asList("#api", "#json", "#toon")),
                new Post(1004, "sarah_codes", "New blog post: Integrating TOON with Spring Boot", "2024-01-15T07:20:00", 234, 45, Arrays.asList("#springboot", "#java", "#toon")),
                new Post(1005, "mike_dev", "TOON tabular format is perfect for structured data!", "2024-01-14T18:30:00", 67, 8, Arrays.asList("#data", "#format", "#toon"))
        );

        Feed feed = new Feed("trending", posts, new Date());

        String toon = converter.toToon(feed);
        String json = jsonMapper.writeValueAsString(feed);

        System.out.println("\n=== Social Media Feed ===");
        System.out.println("TOON Output:");
        System.out.println(toon);
        System.out.println("\nSize comparison:");
        System.out.println("TOON: " + toon.length() + " chars");
        System.out.println("JSON: " + json.length() + " chars");
        System.out.println("Reduction: " + calculateReduction(json.length(), toon.length()) + "%");

        assertThat(toon).contains("posts[5]:");
        assertThat(toon).contains("tags[3]: #toon,#coding,#optimization");
    }

    // Helper methods
    private double calculateReduction(int original, int reduced) {
        return Math.round(((double)(original - reduced) / original) * 100 * 10) / 10.0;
    }

    private double random(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    private Module createModule(int id, String title, List<Lesson> lessons) {
        Module module = new Module();
        module.setModuleId(id);
        module.setTitle(title);
        module.setLessons(lessons);
        return module;
    }

    // Test model classes for scenarios

    // Chat/LLM Models
    @Data
    @ToonSerializable
    static class ChatSession {
        private String sessionId;
        private String userId;
        private String model;
        private List<ChatMessage> messages;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class ChatMessage {
        @ToonField(order = 1)
        private String role;
        @ToonField(order = 2)
        private String content;
        @ToonField(order = 3)
        private String timestamp;
        @ToonField(order = 4)
        private int tokenCount;
    }

    // IoT Models
    @Data
    @ToonSerializable
    static class SensorDevice {
        private String deviceId;
        private String location;
        private String type;
        private List<SensorReading> readings;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class SensorReading {
        @ToonField(order = 1)
        private String timestamp;
        @ToonField(order = 2)
        private double temperature;
        @ToonField(order = 3)
        private double humidity;
        @ToonField(order = 4)
        private double pressure;
        @ToonField(order = 5)
        private boolean alert;
    }

    // ML Models
    @Data
    @ToonSerializable
    static class MLDataset {
        private String name;
        private String version;
        private List<DataPoint> data;
        private Map<String, Object> metadata;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class DataPoint {
        @ToonField(order = 1)
        private int id;
        @ToonField(order = 2)
        private List<Double> features;
        @ToonField(order = 3)
        private int label;
    }

    // Microservices Models
    @Data
    @ToonSerializable
    static class ServiceMesh {
        private String environment;
        private String region;
        private List<ServiceConfig> services;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class ServiceConfig {
        @ToonField(order = 1)
        private String name;
        @ToonField(order = 2)
        private String version;
        @ToonField(order = 3)
        private int replicas;
        @ToonField(order = 4)
        private int minMemoryMB;
        @ToonField(order = 5)
        private int maxMemoryMB;
        @ToonField(order = 6)
        private List<String> endpoints;
    }

    // Analytics Models
    @Data
    @ToonSerializable
    static class Dashboard {
        private String dashboardId;
        private String title;
        private List<Metric> metrics;
        private List<ChartData> chartData;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Metric {
        @ToonField(order = 1)
        private String name;
        @ToonField(order = 2)
        private double current;
        @ToonField(order = 3)
        private double previous;
        @ToonField(order = 4)
        private String change;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class ChartData {
        @ToonField(order = 1)
        private String period;
        @ToonField(order = 2)
        private List<Double> values;
    }

    // E-learning Models
    @Data
    @ToonSerializable
    static class Course {
        private String courseId;
        private String title;
        private String instructor;
        private List<Module> modules;
    }

    @Data
    @ToonSerializable
    static class Module {
        private int moduleId;
        private String title;
        private List<Lesson> lessons;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Lesson {
        @ToonField(order = 1)
        private int lessonId;
        @ToonField(order = 2)
        private String title;
        @ToonField(order = 3)
        private int durationMinutes;
        @ToonField(order = 4)
        private boolean completed;
    }

    // Social Media Models
    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Feed {
        private String feedType;
        private List<Post> posts;
        @ToonField(format = "yyyy-MM-dd HH:mm:ss")
        private Date generatedAt;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Post {
        @ToonField(order = 1)
        private int postId;
        @ToonField(order = 2)
        private String author;
        @ToonField(order = 3)
        private String content;
        @ToonField(order = 4)
        private String timestamp;
        @ToonField(order = 5)
        private int likes;
        @ToonField(order = 6)
        private int comments;
        @ToonField(order = 7)
        private List<String> tags;
    }
}