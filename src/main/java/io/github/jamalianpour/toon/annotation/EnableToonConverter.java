package io.github.jamalianpour.toon.annotation;

import io.github.jamalianpour.toon.config.ToonConverterAutoConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * Enables TOON converter functionality in a Spring Boot application.
 *
 * @author Mohammad Jamalianpour
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ToonConverterAutoConfiguration.class)
public @interface EnableToonConverter {
    /**
     * Default indentation for nested objects
     */
    String indent() default "  ";

    /**
     * Whether to use compact mode by default
     */
    boolean compactMode() default false;
}