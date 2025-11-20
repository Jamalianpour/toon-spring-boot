package io.github.jamalianpour.toon.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as TOON serializable.
 * Classes annotated with this will be eligible for TOON format conversion.
 *
 * @author Mohammad Jamalianpour
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToonSerializable {
    /**
     * Custom name for the root element (optional)
     */
    String value() default "";

    /**
     * Whether to include null values in the output
     */
    boolean includeNulls() default false;

    /**
     * Whether to use compact mode for arrays
     */
    boolean compactArrays() default true;
}