package io.github.jamalianpour.toon.annotation;

import java.lang.annotation.*;

/**
 * Excludes a field from TOON serialization.
 *
 * @author Mohammad Jamalianpour
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToonIgnore {
}