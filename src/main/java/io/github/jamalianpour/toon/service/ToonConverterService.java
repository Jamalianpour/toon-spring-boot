package io.github.jamalianpour.toon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamalianpour.toon.annotation.ToonSerializable;
import io.github.jamalianpour.toon.core.ToonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Service for TOON conversion operations.
 *
 * @author Mohammad Jamalianpour
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToonConverterService {

    private final ToonConverter converter;

    /**
     * Converts an object to TOON format string.
     *
     * @param obj The object to convert
     * @return TOON formatted string
     */
    public String convert(Object obj) {
        if (obj == null) {
            return "null";
        }

        // Check if object is annotated with @ToonSerializable
        Class<?> clazz = obj.getClass();
        if (!isConvertible(clazz)) {
            log.warn("Class {} is not annotated with @ToonSerializable. Consider adding the annotation for better control.",
                    clazz.getName());
        }

        return converter.toToon(obj);
    }

    /**
     * Converts an object to TOON format and writes to a file.
     *
     * @param obj  The object to convert
     * @param file The output file
     */
    public void convertToFile(Object obj, File file) throws IOException {
        String toon = convert(obj);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(toon);
        }
    }

    /**
     * Converts an object to TOON format and writes to a path.
     *
     * @param obj  The object to convert
     * @param path The output path
     */
    public void convertToPath(Object obj, Path path) throws IOException {
        String toon = convert(obj);
        Files.writeString(path, toon, StandardCharsets.UTF_8);
    }

    /**
     * Converts an object to TOON format and writes to an output stream.
     *
     * @param obj          The object to convert
     * @param outputStream The output stream
     */
    public void convertToStream(Object obj, OutputStream outputStream) throws IOException {
        String toon = convert(obj);
        outputStream.write(toon.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Converts an object to TOON format and returns as byte array.
     *
     * @param obj The object to convert
     * @return TOON formatted data as bytes
     */
    public byte[] convertToBytes(Object obj) {
        String toon = convert(obj);
        return toon.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Batch converts multiple objects to TOON format.
     *
     * @param objects The objects to convert
     * @return Map of objects to their TOON representations
     */
    public Map<Object, String> batchConvert(Collection<?> objects) {
        return objects.stream()
                .collect(java.util.stream.Collectors.toMap(
                        obj -> obj,
                        this::convert
                ));
    }

    /**
     * Validates if an object can be converted to TOON format.
     *
     * @param obj The object to validate
     * @return true if the object can be converted
     */
    public boolean canConvert(Object obj) {
        if (obj == null) {
            return true;
        }

        Class<?> clazz = obj.getClass();
        return isConvertible(clazz);
    }

    /**
     * Checks if a class is convertible (has proper annotations or is a basic type).
     */
    private boolean isConvertible(Class<?> clazz) {
        // Primitives, collections, and maps are always convertible
        if (clazz.isPrimitive() ||
                Collection.class.isAssignableFrom(clazz) ||
                Map.class.isAssignableFrom(clazz) ||
                clazz.isArray()) {
            return true;
        }

        // Check for ToonSerializable annotation
        return clazz.isAnnotationPresent(ToonSerializable.class);
    }

    /**
     * Gets the estimated token reduction compared to JSON.
     *
     * @param obj The object to analyze
     * @return Estimated percentage of token reduction
     */
    public double estimateTokenReduction(Object obj) {
        try {
            String toon = convert(obj);
            String json = new ObjectMapper()
                    .writeValueAsString(obj);

            int toonTokens = estimateTokens(toon);
            int jsonTokens = estimateTokens(json);

            if (jsonTokens == 0) return 0;

            double reduction = ((double) (jsonTokens - toonTokens) / jsonTokens) * 100;
            log.info("TOON tokens: {}, JSON tokens: {}, Reduction: {:.1f}%",
                    toonTokens, jsonTokens, reduction);

            return reduction;
        } catch (Exception e) {
            log.error("Failed to estimate token reduction", e);
            return 0;
        }
    }

    /**
     * Simple token estimation (counts characters and special symbols).
     * For production, consider using a proper tokenizer.
     */
    private int estimateTokens(String text) {
        // Simple heuristic: count words and special characters
        return text.split("\\s+|(?=[,.:;{}\\[\\]\"'])").length;
    }
}