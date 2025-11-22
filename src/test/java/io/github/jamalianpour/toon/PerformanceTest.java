package io.github.jamalianpour.toon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamalianpour.toon.annotation.ToonField;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import io.github.jamalianpour.toon.core.ToonConverter;
import io.github.jamalianpour.toon.service.ToonConverterService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Performance and token reduction tests.
 */
class PerformanceTest {

    private ToonConverter converter;
    private ToonConverterService service;
    private ObjectMapper jsonMapper;

    @BeforeEach
    void setUp() {
        converter = new ToonConverter();
        service = new ToonConverterService(converter);
        jsonMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should achieve significant token reduction for tabular data")
    void testTabularDataTokenReduction() throws Exception {
        List<Record> records = generateRecords(100);

        String toon = converter.toToon(records);
        String json = jsonMapper.writeValueAsString(records);

        System.out.println("TOON size: " + toon.length() + " characters");
        System.out.println("JSON size: " + json.length() + " characters");

        double reduction = ((double)(json.length() - toon.length()) / json.length()) * 100;
        System.out.println("Size reduction: " + String.format("%.1f%%", reduction));

        assertThat(toon.length()).isLessThan(json.length());
        assertThat(reduction).isGreaterThan(30); // Should achieve at least 30% reduction
    }

    @Test
    @DisplayName("Should handle large dataset conversion efficiently")
    void testLargeDatasetPerformance() {
        List<Record> records = generateRecords(1000);

        long startTime = System.currentTimeMillis();
        String result = converter.toToon(records);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Conversion of 1000 records took: " + duration + "ms");

        assertThat(duration).isLessThan(1000); // Should complete within 1 second
        assertThat(result).contains("[1000]{id,name,email,age,active,score}:");
    }

    @Test
    @DisplayName("Should estimate token reduction accurately")
    void testTokenReductionEstimation() {
        List<User> users = Arrays.asList(
                new User(1, "Alice Johnson", "alice@example.com", 30, true, 95.5),
                new User(2, "Bob Smith", "bob@example.com", 25, true, 87.3),
                new User(3, "Charlie Brown", "charlie@example.com", 35, false, 92.1),
                new User(4, "Diana Prince", "diana@example.com", 28, true, 98.7),
                new User(5, "Edward Norton", "edward@example.com", 42, true, 76.4)
        );

        double reduction = service.estimateTokenReduction(users);
        System.out.println("Estimated token reduction: " + String.format("%.1f%%", reduction));

        assertThat(reduction).isGreaterThan(25);
    }

    @Test
    @DisplayName("Should compare different data structures")
    void testDifferentStructuresComparison() throws Exception {
        // Flat structure (best case for TOON)
        List<FlatData> flatData = generateFlatData(50);
        compareSizes("Flat Data", flatData);

        // Nested structure (less optimal)
        // TOON is not good for large nested objects
        List<NestedData> nestedData = generateNestedData(50);
        compareSizes("Nested Data", nestedData);

        // Mixed structure
        Map<String, Object> mixedData = generateMixedData();
        compareSizes("Mixed Data", mixedData);
    }

    private void compareSizes(String label, Object data) throws Exception {
        String toon = converter.toToon(data);
        String json = jsonMapper.writeValueAsString(data);

        double reduction = ((double)(json.length() - toon.length()) / json.length()) * 100;

        System.out.println("\n" + label + ":");
        System.out.println("  TOON: " + toon.length() + " chars");
        System.out.println("  JSON: " + json.length() + " chars");
        System.out.println("  Reduction: " + String.format("%.1f%%", reduction));
    }

    private List<Record> generateRecords(int count) {
        List<Record> records = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            records.add(new Record(
                    i,
                    "User" + i,
                    "user" + i + "@example.com",
                    20 + (i % 50),
                    i % 2 == 0,
                    50.0 + (i % 50)
            ));
        }
        return records;
    }

    private List<FlatData> generateFlatData(int count) {
        List<FlatData> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add(new FlatData(i, "Item" + i, "Description" + i));
        }
        return data;
    }

    private List<NestedData> generateNestedData(int count) {
        List<NestedData> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            NestedData item = new NestedData();
            item.setId(i);
            item.setMeta(new Meta("Type" + i, "Category" + i));
            item.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            data.add(item);
        }
        return data;
    }

    private Map<String, Object> generateMixedData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("version", "1.0.0");
        data.put("count", 100);
        data.put("items", generateFlatData(10));
        data.put("config", Map.of("key1", "value1", "key2", "value2"));
        return data;
    }

    // Test model classes
    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Record {
        @ToonField(order = 1)
        private int id;
        @ToonField(order = 2)
        private String name;
        @ToonField(order = 3)
        private String email;
        @ToonField(order = 4)
        private int age;
        @ToonField(order = 5)
        private boolean active;
        @ToonField(order = 6)
        private double score;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class User {
        private int id;
        private String name;
        private String email;
        private int age;
        private boolean active;
        private double score;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class FlatData {
        private int id;
        private String name;
        private String description;
    }

    @Data
    @ToonSerializable
    static class NestedData {
        private int id;
        private Meta meta;
        private List<String> tags;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Meta {
        private String type;
        private String category;
    }
}