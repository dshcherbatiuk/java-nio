package com.nio.exception;

public class ConnectionReadingException extends RuntimeException {
    public ConnectionReadingException(final String message) {
        super(message);
    }
}
