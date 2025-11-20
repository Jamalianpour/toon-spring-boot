package io.github.jamalianpour.toon.annotation;

import java.lang.annotation.*;

/**
 * Configures array serialization behavior in TOON format.
 *
 * @author Mohammad Jamalianpour
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToonArray {
    /**
     * Whether to use inline format for simple arrays
     */
    boolean inline() default true;

    /**
     * Custom delimiter for array elements (default is comma)
     */
    String delimiter() default ",";

    /**
     * Whether to use tabular format for object arrays
     */
    boolean tabular() default true;
}
