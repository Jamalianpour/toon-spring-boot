package io.github.jamalianpour.toon.annotation;

import java.lang.annotation.*;

/**
 * Customizes how a field is serialized to TOON format.
 *
 * @author Mohammad Jamalianpour
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToonField {
    /**
     * Custom field name in TOON output
     */
    String value() default "";

    /**
     * Order of the field in tabular arrays (lower values come first)
     */
    int order() default Integer.MAX_VALUE;

    /**
     * Whether to include this field in TOON output
     */
    boolean include() default true;

    /**
     * Format pattern for dates, numbers, etc.
     */
    String format() default "";
}