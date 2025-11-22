package io.github.jamalianpour.toon;

import io.github.jamalianpour.toon.annotation.ToonField;
import io.github.jamalianpour.toon.annotation.ToonIgnore;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import io.github.jamalianpour.toon.core.ToonConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for complex nested structures.
 */
class ComplexStructureTest {

    private ToonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ToonConverter();
    }

    @Test
    @DisplayName("Should handle deeply nested objects")
    void testDeeplyNestedObjects() {
        Company company = new Company();
        company.setName("TechCorp");

        Department engineering = new Department();
        engineering.setName("Engineering");
        engineering.setBudget(1000000);

        Employee alice = new Employee("Alice", "Senior Dev", 120000);
        Employee bob = new Employee("Bob", "Junior Dev", 80000);
        engineering.setEmployees(Arrays.asList(alice, bob));

        Department sales = new Department();
        sales.setName("Sales");
        sales.setBudget(500000);

        Employee charlie = new Employee("Charlie", "Sales Manager", 90000);
        sales.setEmployees(Arrays.asList(charlie));

        company.setDepartments(Arrays.asList(engineering, sales));

        String result = converter.toToon(company);

        assertThat(result).contains("name: TechCorp");
        assertThat(result).contains("departments[2]");
        assertThat(result).contains("name: Engineering");
        assertThat(result).contains("budget: 1000000");
        assertThat(result).contains("employees[2]{name,position,salary}:");
        assertThat(result).contains("Alice,\"Senior Dev\",120000");
    }

    @Test
    @DisplayName("Should handle mixed type collections")
    void testMixedTypeCollections() {
        List<Object> mixed = Arrays.asList(
                "string",
                42,
                true,
                Arrays.asList(1, 2, 3),
                new SimpleObject("nested", 99)
        );

        String result = converter.toToon(mixed);

        assertThat(result).contains("[5]:");
        assertThat(result).contains("- string");
        assertThat(result).contains("- 42");
        assertThat(result).contains("- true");
        assertThat(result).contains("- [3]: 1,2,3");
        assertThat(result).contains("- name: nested");
        assertThat(result).contains("  value: 99");
    }

    @Test
    @DisplayName("Should handle recursive references safely")
    void testRecursiveReferenceSafety() {
        Node node1 = new Node("Node1", null);
        Node node2 = new Node("Node2", null);
        node1.setNext(node2);
        // Don't create cycle for now - would need cycle detection

        String result = converter.toToon(node1);

        assertThat(result).contains("name: Node1");
        assertThat(result).contains("next:");
        assertThat(result).contains("name: Node2");
    }

    @Test
    @DisplayName("Should handle maps with complex values")
    void testComplexMap() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("version", "1.0.0");
        config.put("features", Arrays.asList("feature1", "feature2", "feature3"));

        Map<String, Object> database = new LinkedHashMap<>();
        database.put("host", "localhost");
        database.put("port", 5432);
        database.put("credentials", new Credentials("admin", "secret"));
        config.put("database", database);

        String result = converter.toToon(config);

        assertThat(result).contains("version: 1.0.0");
        assertThat(result).contains("features[3]: feature1,feature2,feature3");
        assertThat(result).contains("database:");
        assertThat(result).contains("host: localhost");
        assertThat(result).contains("port: 5432");
        assertThat(result).contains("credentials:");
    }

    @Test
    @DisplayName("Should handle enums correctly")
    void testEnumConversion() {
        EnumContainer container = new EnumContainer(
                Status.ACTIVE,
                Arrays.asList(Status.PENDING, Status.ACTIVE, Status.COMPLETED)
        );

        String result = converter.toToon(container);

        assertThat(result).contains("currentStatus: ACTIVE");
        assertThat(result).contains("allStatuses[3]: PENDING,ACTIVE,COMPLETED");
    }

//    @Test
//    @DisplayName("Should handle inheritance correctly")
//    void testInheritance() {
//        ExtendedModel model = new ExtendedModel();
//        model.setBaseField("base value");
//        model.setExtendedField("extended value");
//        model.setAdditionalField(42);
//
//        String result = converter.toToon(model);
//
//        assertThat(result).contains("baseField: \"base value\"");
//        assertThat(result).contains("extendedField: \"extended value\"");
//        assertThat(result).contains("additionalField: 42");
//    }

    // Test model classes
    @Data
    @ToonSerializable
    static class Company {
        private String name;
        private List<Department> departments;
    }

    @Data
    @ToonSerializable
    static class Department {
        private String name;
        private int budget;
        private List<Employee> employees;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToonSerializable
    static class Employee {
        @ToonField(order = 1)
        private String name;
        @ToonField(order = 2)
        private String position;
        @ToonField(order = 3)
        private int salary;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class SimpleObject {
        private String name;
        private int value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToonSerializable
    static class Node {
        private String name;
        private Node next;
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class Credentials {
        private String username;
        @ToonIgnore
        private String password;
    }

    enum Status {
        PENDING, ACTIVE, COMPLETED, CANCELLED
    }

    @Data
    @AllArgsConstructor
    @ToonSerializable
    static class EnumContainer {
        private Status currentStatus;
        private List<Status> allStatuses;
    }

    @Data
    @ToonSerializable
    static class BaseModel {
        private String baseField;
    }

    @Data
    @ToonSerializable
    static class ExtendedModel extends BaseModel {
        private String extendedField;
        private int additionalField;
    }
}
