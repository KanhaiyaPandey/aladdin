package com.store.aladdin.validations;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}