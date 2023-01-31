package com.nio.tcp.exception;

public class ReadConnectionException extends RuntimeException {
    public ReadConnectionException(final String message) {
        super(message);
    }
}