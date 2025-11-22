package io.github.jamalianpour.toon;

import io.github.jamalianpour.toon.core.ToonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Basic conversion tests for TOON converter.
 */
class BasicConversionTest {

    private ToonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ToonConverter();
    }

    @Test
    @DisplayName("Should convert null to 'null'")
    void testNullConversion() {
        String result = converter.toToon(null);
        assertThat(result).isEqualTo("null");
    }

    @Test
    @DisplayName("Should convert primitives correctly")
    void testPrimitiveConversion() {
        assertThat(converter.toToon(42)).isEqualTo("42");
        assertThat(converter.toToon(3.14)).isEqualTo("3.14");
        assertThat(converter.toToon(true)).isEqualTo("true");
        assertThat(converter.toToon(false)).isEqualTo("false");
        assertThat(converter.toToon('A')).isEqualTo("A");
    }

    @Test
    @DisplayName("Should convert strings with proper quoting")
    void testStringConversion() {
        assertThat(converter.toToon("simple")).isEqualTo("simple");
        assertThat(converter.toToon("hello world")).isEqualTo("\"hello world\"");
        assertThat(converter.toToon("with,comma")).isEqualTo("\"with,comma\"");
        assertThat(converter.toToon("with:colon")).isEqualTo("\"with:colon\"");
        assertThat(converter.toToon("true")).isEqualTo("\"true\""); // Reserved word
        assertThat(converter.toToon("123")).isEqualTo("\"123\""); // Looks like number
        assertThat(converter.toToon("")).isEqualTo("");
    }

    @Test
    @DisplayName("Should convert empty collections")
    void testEmptyCollections() {
        assertThat(converter.toToon(new ArrayList<>())).isEqualTo("[]");
        assertThat(converter.toToon(new HashSet<>())).isEqualTo("[]");
        assertThat(converter.toToon(new LinkedList<>())).isEqualTo("[]");
        assertThat(converter.toToon(new HashMap<>())).isEqualTo("{}");
    }

    @Test
    @DisplayName("Should convert primitive arrays inline")
    void testPrimitiveArrays() {
        assertThat(converter.toToon(new int[]{1, 2, 3}))
                .isEqualTo("[3]: 1,2,3");

        assertThat(converter.toToon(new double[]{1.1, 2.2, 3.3}))
                .isEqualTo("[3]: 1.1,2.2,3.3");

        assertThat(converter.toToon(new boolean[]{true, false, true}))
                .isEqualTo("[3]: true,false,true");

        assertThat(converter.toToon(new String[]{"a", "b", "c"}))
                .isEqualTo("[3]: a,b,c");
    }

    @Test
    @DisplayName("Should convert primitive collections inline")
    void testPrimitiveCollections() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        assertThat(converter.toToon(numbers)).isEqualTo("[5]: 1,2,3,4,5");

        List<String> words = Arrays.asList("hello", "world", "test");
        assertThat(converter.toToon(words)).isEqualTo("[3]: hello,world,test");

        Set<Boolean> bools = new LinkedHashSet<>(Arrays.asList(true, false));
        assertThat(converter.toToon(bools)).isEqualTo("[2]: true,false");
    }

    @Test
    @DisplayName("Should handle collections with spaces and special chars")
    void testCollectionsWithSpecialChars() {
        List<String> items = Arrays.asList("hello world", "test,comma", "with:colon");
        assertThat(converter.toToon(items))
                .isEqualTo("[3]: \"hello world\",\"test,comma\",\"with:colon\"");
    }

    @Test
    @DisplayName("Should convert simple maps")
    void testSimpleMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Alice");
        map.put("age", 30);
        map.put("active", true);

        String result = converter.toToon(map);
        assertThat(result).contains("name: Alice");
        assertThat(result).contains("age: 30");
        assertThat(result).contains("active: true");
    }

    @Test
    @DisplayName("Should handle maps with null values")
    void testMapWithNulls() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("field1", "value");
        map.put("field2", null);
        map.put("field3", 123);

        String result = converter.toToon(map);
        assertThat(result).contains("field1: value");
        assertThat(result).contains("field2: null");
        assertThat(result).contains("field3: 123");
    }
}
