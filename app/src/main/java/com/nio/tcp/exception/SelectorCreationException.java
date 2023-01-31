package com.nio.tcp.exception;

public class SelectorCreationException extends RuntimeException {
    public SelectorCreationException(final String message) {
        super(message);
    }
}