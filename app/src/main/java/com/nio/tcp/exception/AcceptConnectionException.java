package com.nio.tcp.exception;

public class AcceptConnectionException extends RuntimeException {
    public AcceptConnectionException(final String message) {
        super(message);
    }
}