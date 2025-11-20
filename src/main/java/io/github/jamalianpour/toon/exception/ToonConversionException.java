package io.github.jamalianpour.toon.exception;

/**
 * Exception thrown when TOON conversion fails.
 *
 * @author Mohammad Jamalianpour
 */
public class ToonConversionException extends RuntimeException {

    public ToonConversionException(String message) {
        super(message);
    }

    public ToonConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToonConversionException(Throwable cause) {
        super(cause);
    }
}