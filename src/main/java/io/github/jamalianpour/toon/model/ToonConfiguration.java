package io.github.jamalianpour.toon.model;

import lombok.Data;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for TOON converter.
 *
 * @author Mohammad Jamalianpour
 */
@Data
@Builder
@Configuration
@ConfigurationProperties(prefix = "toon.converter")
public class ToonConfiguration {

    /**
     * Indentation string for nested objects
     */
    @Builder.Default
    private String indent = "  ";

    /**
     * Whether to include null values by default
     */
    @Builder.Default
    private boolean includeNulls = false;

    /**
     * Whether to use compact mode for arrays by default
     */
    @Builder.Default
    private boolean compactArrays = true;

    /**
     * Custom delimiter for array elements
     */
    @Builder.Default
    private String arrayDelimiter = ",";

    /**
     * Whether to use tabular format for uniform object arrays
     */
    @Builder.Default
    private boolean tabularArrays = true;

    /**
     * Maximum depth for nested objects (to prevent infinite recursion)
     */
    @Builder.Default
    private int maxDepth = 100;

    /**
     * Whether to quote field names that contain special characters
     */
    @Builder.Default
    private boolean quoteFieldNames = false;

    /**
     * Default date format pattern
     */
    @Builder.Default
    private String defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static ToonConfiguration defaults() {
        return ToonConfiguration.builder().build();
    }
}