package io.github.jamalianpour.toon;

import io.github.jamalianpour.toon.annotation.ToonField;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import io.github.jamalianpour.toon.core.ToonConverter;
import io.github.jamalianpour.toon.example.HikeExample;
import io.github.jamalianpour.toon.model.ToonConfiguration;
import io.github.jamalianpour.toon.service.ToonConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for TOON converter.
 *
 * @author Mohammad Jamalianpour
 */
class ToonConverterTest {

    private ToonConverter converter;
    private ToonConverterService service;

    @BeforeEach
    void setUp() {
        ToonConfiguration config = ToonConfiguration.defaults();
        converter = new ToonConverter(config);
        service = new ToonConverterService(converter);
    }

    @Test
    void testHikeExampleConversion() {
        // Create the example data
        HikeExample example = new HikeExample();

        // Context
        Map<String, String> context = new LinkedHashMap<>();
        context.put("task", "Our favorite hikes together");
        context.put("location", "Boulder");
        context.put("season", "spring_2025");
        example.setContext(context);

        // Friends
        example.setFriends(Arrays.asList("ana", "luis", "sam"));

        // Hikes
        List<HikeExample.Hike> hikes = new ArrayList<>();
        hikes.add(new HikeExample.Hike(1, "Blue Lake Trail", 7.5, 320, "ana", true));
        hikes.add(new HikeExample.Hike(2, "Ridge Overlook", 9.2, 540, "luis", false));
        hikes.add(new HikeExample.Hike(3, "Wildflower Loop", 5.1, 180, "sam", true));
        example.setHikes(hikes);

        // Convert to TOON
        String toon = converter.toToon(example);

        System.out.println("Generated TOON:");
        System.out.println(toon);
        System.out.println();

        // Verify the output contains expected patterns
        assertThat(toon).contains("context:")
                .contains("task: \"Our favorite hikes together\"")
                .contains("location: Boulder")
                .contains("season: spring_2025")
                .contains("friends[3]: ana,luis,sam")
                .contains("hikes[3]{id,name,distanceKm,elevationGain,companion,wasSunny}:")
                .contains("1,\"Blue Lake Trail\",7.5,320,ana,true");
    }

    @Test
    void testTokenReductionEstimation() {
        // Create sample data
        List<User> users = Arrays.asList(
                new User(1, "Alice", "admin", "alice@example.com"),
                new User(2, "Bob", "user", "bob@example.com"),
                new User(3, "Charlie", "editor", "charlie@example.com")
        );

        String toon = converter.toToon(users);
        System.out.println("Users TOON:");
        System.out.println(toon);

        double reduction = service.estimateTokenReduction(users);
        System.out.println("Estimated token reduction: " + reduction + "%");

        assertThat(reduction).isGreaterThan(30); // Should achieve at least 30% reduction
    }

    @Test
    void testPrimitiveArrayConversion() {
        int[] numbers = {1, 2, 3, 4, 5};
        String toon = converter.toToon(numbers);

        System.out.println("Numbers TOON: " + toon);
        assertThat(toon).isEqualTo("[5]: 1,2,3,4,5");
    }

    @Test
    void testNestedObjectConversion() {
        Company company = new Company();
        company.setName("TechCorp");
        company.setEmployees(100);

        Address address = new Address();
        address.setStreet("123 Tech St");
        address.setCity("San Francisco");
        address.setZip("94107");
        company.setAddress(address);

        String toon = converter.toToon(company);

        System.out.println("Company TOON:");
        System.out.println(toon);

        assertThat(toon).contains("name: TechCorp");
        assertThat(toon).contains("employees: 100");
        assertThat(toon).contains("address:");
        assertThat(toon).contains("street: \"123 Tech St\"");
    }

    // Test model classes
    @lombok.Data
    @lombok.AllArgsConstructor
    @ToonSerializable
    static class User {
        @ToonField(order = 1)
        private int id;

        @ToonField(order = 2)
        private String name;

        @ToonField(order = 3)
        private String role;

        @ToonField(order = 4)
        private String email;
    }

    @lombok.Data
    @ToonSerializable
    static class Company {
        private String name;
        private int employees;
        private Address address;
    }

    @lombok.Data
    @ToonSerializable
    static class Address {
        private String street;
        private String city;
        private String zip;
    }
}