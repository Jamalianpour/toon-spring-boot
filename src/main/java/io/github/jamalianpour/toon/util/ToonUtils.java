package io.github.jamalianpour.toon.util;

import io.github.jamalianpour.toon.core.ToonConverter;
import io.github.jamalianpour.toon.model.ToonConfiguration;

/**
 * Utility methods for TOON conversion.
 *
 * @author Mohammad Jamalianpour
 */
public final class ToonUtils {

    private static final ToonConverter DEFAULT_CONVERTER = new ToonConverter();

    private ToonUtils() {
        // Utility class
    }

    /**
     * Quick conversion to TOON format using default settings.
     *
     * @param obj The object to convert
     * @return TOON formatted string
     */
    public static String toToon(Object obj) {
        return DEFAULT_CONVERTER.toToon(obj);
    }

    /**
     * Quick conversion to TOON format with custom configuration.
     *
     * @param obj The object to convert
     * @param config Custom configuration
     * @return TOON formatted string
     */
    public static String toToon(Object obj, ToonConfiguration config) {
        return new ToonConverter(config).toToon(obj);
    }

    /**
     * Creates a TOON converter builder.
     *
     * @return Builder for creating custom converter
     */
    public static ToonConverterBuilder builder() {
        return new ToonConverterBuilder();
    }

    /**
     * Builder for creating custom TOON converters.
     */
    public static class ToonConverterBuilder {
        private final ToonConfiguration.ConfigurationBuilder configBuilder =
                ToonConfiguration.builder();

        public ToonConverterBuilder withIndent(String indent) {
            configBuilder.indent(indent);
            return this;
        }

        public ToonConverterBuilder includeNulls() {
            configBuilder.includeNulls(true);
            return this;
        }

        public ToonConverterBuilder withDateFormat(String format) {
            configBuilder.defaultDateFormat(format);
            return this;
        }

        public ToonConverter build() {
            return new ToonConverter(configBuilder.build());
        }
    }
}