package com.cryptographydemo.securenotes.exception;

/**
 * Simple runtime exception mapped to HTTP 404 by RestExceptionHandler.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
