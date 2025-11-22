package io.github.jamalianpour.toon.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for TOON converter.
 *
 * @author Mohammad Jamalianpour
 */
@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "toon.converter")
public class ToonConfiguration {

    /**
     * Indentation string for nested objects
     */
    private String indent = "  ";

    /**
     * Whether to include null values by default
     */
    private boolean includeNulls = false;

    /**
     * Whether to use compact mode for arrays by default
     */
    private boolean compactArrays = true;

    /**
     * Custom delimiter for array elements
     */
    private String arrayDelimiter = ",";

    /**
     * Whether to use tabular format for uniform object arrays
     */
    private boolean tabularArrays = true;

    /**
     * Maximum depth for nested objects (to prevent infinite recursion)
     */
    private int maxDepth = 100;

    /**
     * Whether to quote field names that contain special characters
     */
    private boolean quoteFieldNames = false;

    /**
     * Default date format pattern
     */
    private String defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Creates a default configuration instance.
     *
     * @return ToonConfiguration with default values
     */
    public static ToonConfiguration defaults() {
        return new ToonConfiguration();
    }

    /**
     * Creates a builder for ToonConfiguration.
     *
     * @return ConfigurationBuilder instance
     */
    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    /**
     * Builder for ToonConfiguration.
     */
    public static class ConfigurationBuilder {
        private final ToonConfiguration config;

        public ConfigurationBuilder() {
            this.config = new ToonConfiguration();
        }

        public ConfigurationBuilder indent(String indent) {
            config.indent = indent;
            return this;
        }

        public ConfigurationBuilder includeNulls(boolean includeNulls) {
            config.includeNulls = includeNulls;
            return this;
        }

        public ConfigurationBuilder compactArrays(boolean compactArrays) {
            config.compactArrays = compactArrays;
            return this;
        }

        public ConfigurationBuilder arrayDelimiter(String arrayDelimiter) {
            config.arrayDelimiter = arrayDelimiter;
            return this;
        }

        public ConfigurationBuilder tabularArrays(boolean tabularArrays) {
            config.tabularArrays = tabularArrays;
            return this;
        }

        public ConfigurationBuilder maxDepth(int maxDepth) {
            config.maxDepth = maxDepth;
            return this;
        }

        public ConfigurationBuilder quoteFieldNames(boolean quoteFieldNames) {
            config.quoteFieldNames = quoteFieldNames;
            return this;
        }

        public ConfigurationBuilder defaultDateFormat(String defaultDateFormat) {
            config.defaultDateFormat = defaultDateFormat;
            return this;
        }

        public ToonConfiguration build() {
            return config;
        }
    }
}