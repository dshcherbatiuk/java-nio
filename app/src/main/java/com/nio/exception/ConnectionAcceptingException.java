package com.nio.exception;

public class ConnectionAcceptingException extends RuntimeException {
    public ConnectionAcceptingException(final String message) {
        super(message);
    }
}
