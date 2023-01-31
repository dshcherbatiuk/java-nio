package com.nio.tcp.exception;

public class OperationNotPresentException extends RuntimeException {
    public OperationNotPresentException(final String message) {
        super(message);
    }
}