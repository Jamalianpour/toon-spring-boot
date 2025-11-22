package io.github.jamalianpour.toon;

import io.github.jamalianpour.toon.annotation.ToonField;
import io.github.jamalianpour.toon.annotation.ToonIgnore;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import io.github.jamalianpour.toon.core.ToonConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Date;
import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for annotation-based configuration.
 */
class AnnotationTest {

    private ToonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ToonConverter();
    }

    @Test
    @DisplayName("Should respect @ToonIgnore annotation")
    void testToonIgnore() {
        UserWithIgnore user = new UserWithIgnore("john", "secret123", "john@example.com");

        String result = converter.toToon(user);

        assertThat(result).contains("username: john");
        assertThat(result).contains("email: john@example.com");
        assertThat(result).doesNotContain("password");
        assertThat(result).doesNotContain("secret123");
    }

    @Test
    @DisplayName("Should use custom field names from @ToonField")
    void testCustomFieldNames() {
        CustomFieldNames obj = new CustomFieldNames(1, "Test Product", 99.99);

        String result = converter.toToon(obj);

        assertThat(result).contains("product_id: 1");
        assertThat(result).contains("product_name: \"Test Product\"");
        assertThat(result).contains("product_price: 99.99");
    }

    @Test
    @DisplayName("Should respect field order from @ToonField")
    void testFieldOrder() {
        OrderedFields obj = new OrderedFields("Alice", 30, "alice@example.com");

        String result = converter.toToon(obj);
        String[] lines = result.split("\n");

        // Email should come first (order = 1)
        assertThat(lines[0]).isEqualTo("email: alice@example.com");
        // Name should come second (order = 2)
        assertThat(lines[1]).isEqualTo("name: Alice");
        // Age should come last (order = 3)
        assertThat(lines[2]).isEqualTo("age: 30");
    }

    @Test
    @DisplayName("Should exclude fields with include=false")
    void testFieldExclusion() {
        FieldExclusion obj = new FieldExclusion("visible", "hidden");

        String result = converter.toToon(obj);

        assertThat(result).contains("visible: visible");
        assertThat(result).doesNotContain("hidden");
    }

    @Test
    @DisplayName("Should apply date format from @ToonField")
    void testDateFormatting() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = sdf.parse("2024-12-25");

        DateFormatted obj = new DateFormatted(testDate);

        String result = converter.toToon(obj);

        assertThat(result).contains("createdAt: 2024-12-25");
    }

    @Test
    @DisplayName("Should handle @ToonSerializable includeNulls option")
    void testIncludeNulls() {
        WithNulls obj = new WithNulls("value", null);
        String result = converter.toToon(obj);

        assertThat(result).contains("field1: value");
        assertThat(result).contains("field2: null");

        WithoutNulls obj2 = new WithoutNulls("value", null);
        String result2 = converter.toToon(obj2);

        assertThat(result2).contains("field1: value");
        assertThat(result2).doesNotContain("field2");
    }

    // Test model classes
    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class UserWithIgnore {
        private String username;

        @ToonIgnore
        private String password;

        private String email;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class CustomFieldNames {
        @ToonField("product_id")
        private int id;

        @ToonField("product_name")
        private String name;

        @ToonField("product_price")
        private double price;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class OrderedFields {
        @ToonField(order = 2)
        private String name;

        @ToonField(order = 3)
        private int age;

        @ToonField(order = 1)
        private String email;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class FieldExclusion {
        private String visible;

        @ToonField(include = false)
        private String hidden;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class DateFormatted {
        @ToonField(format = "yyyy-MM-dd")
        private Date createdAt;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable(includeNulls = true)
    static class WithNulls {
        private String field1;
        private String field2;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable(includeNulls = false)
    static class WithoutNulls {
        private String field1;
        private String field2;
    }
}
