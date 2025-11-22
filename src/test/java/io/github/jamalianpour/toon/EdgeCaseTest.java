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

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Edge case tests for TOON converter.
 */
class EdgeCaseTest {

    private ToonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ToonConverter();
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyString() {
        assertThat(converter.toToon("")).isEqualTo("");

        List<String> emptyStrings = Arrays.asList("", "", "");
        assertThat(converter.toToon(emptyStrings)).isEqualTo("[3]: ,,");
    }

    @Test
    @DisplayName("Should handle Unicode characters")
    void testUnicodeCharacters() {
        String emoji = "Hello 👋 World 🌍";
        assertThat(converter.toToon(emoji)).isEqualTo("\"Hello 👋 World 🌍\"");

        List<String> unicodeList = Arrays.asList("日本語", "中文", "한국어", "العربية");
        String result = converter.toToon(unicodeList);
        assertThat(result).contains("日本語");
        assertThat(result).contains("中文");
        assertThat(result).contains("한국어");
        assertThat(result).contains("العربية");
    }

    @Test
    @DisplayName("Should handle very large collections")
    void testLargeCollections() {
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeList.add(i);
        }

        String result = converter.toToon(largeList);
        assertThat(result).startsWith("[10000]: 0,1,2,3");
        assertThat(result).endsWith("9999");
    }

    @Test
    @DisplayName("Should handle collections with null elements")
    void testCollectionsWithNulls() {
        List<String> withNulls = Arrays.asList("first", null, "third");
        assertThat(converter.toToon(withNulls)).isEqualTo("[3]: first,null,third");

        List<Object> mixedWithNulls = Arrays.asList("text", null, 123, null, true);
        String result = converter.toToon(mixedWithNulls);
        assertThat(result).contains("[5]: text,null,123,null,true");
    }

    @Test
    @DisplayName("Should handle all primitive array types")
    void testAllPrimitiveArrayTypes() {
        assertThat(converter.toToon(new byte[]{1, 2, 3}))
                .isEqualTo("[3]: 1,2,3");

        assertThat(converter.toToon(new short[]{100, 200, 300}))
                .isEqualTo("[3]: 100,200,300");

        assertThat(converter.toToon(new char[]{'a', 'b', 'c'}))
                .isEqualTo("[3]: a,b,c");

        assertThat(converter.toToon(new float[]{1.1f, 2.2f, 3.3f}))
                .isEqualTo("[3]: 1.1,2.2,3.3");
    }

    @Test
    @DisplayName("Should handle objects with no fields")
    void testEmptyObject() {
        EmptyClass empty = new EmptyClass();
        String result = converter.toToon(empty);
        assertThat(result).isEqualTo("");
    }

    @Test
    @DisplayName("Should handle objects with only ignored fields")
    void testOnlyIgnoredFields() {
        OnlyIgnored obj = new OnlyIgnored("hidden1", "hidden2");
        String result = converter.toToon(obj);
        assertThat(result).isEqualTo("");
    }

    @Test
    @DisplayName("Should handle special number values")
    void testSpecialNumbers() {
        List<Double> specialNumbers = Arrays.asList(
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                0.0,
                -0.0
        );

        String result = converter.toToon(specialNumbers);
        assertThat(result).contains("NaN");
        assertThat(result).contains("Infinity");
        assertThat(result).contains("-Infinity");
    }

    @Test
    @DisplayName("Should handle reserved keywords as values")
    void testReservedKeywords() {
        Map<String, String> reserved = new LinkedHashMap<>();
        reserved.put("true", "true");
        reserved.put("false", "false");
        reserved.put("null", "null");

        String result = converter.toToon(reserved);
        // Keys and string values that look like reserved words should be quoted
        assertThat(result).contains("true: \"true\"");
        assertThat(result).contains("false: \"false\"");
        assertThat(result).contains("null: \"null\"");
    }

    @Test
    @DisplayName("Should handle very long strings")
    void testVeryLongStrings() {
        StringBuilder longStr = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longStr.append("a");
        }

        String result = converter.toToon(longStr.toString());
        assertThat(result).hasSize(10000);
        assertThat(result).matches("a{10000}");
    }

    // Test model classes
    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class SimpleData {
        private String name;
        private int value;
    }

    @ToonSerializable
    static class EmptyClass {
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class OnlyIgnored {
        @ToonIgnore
        private String field1;

        @ToonField(include = false)
        private String field2;
    }
}
