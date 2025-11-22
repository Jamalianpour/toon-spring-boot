package io.github.jamalianpour.toon.example;

import io.github.jamalianpour.toon.annotation.ToonField;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Example model matching the provided JSON/TOON example.
 *
 * @author Mohammad Jamalianpour
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToonSerializable
public class HikeExample {

    private Map<String, String> context;
    private List<String> friends;
    private List<Hike> hikes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToonSerializable
    public static class Hike {
        @ToonField(order = 1)
        private int id;

        @ToonField(order = 2)
        private String name;

        @ToonField(order = 3)
        private double distanceKm;

        @ToonField(order = 4)
        private int elevationGain;

        @ToonField(order = 5)
        private String companion;

        @ToonField(order = 6)
        private boolean wasSunny;
    }
}