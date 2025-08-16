package com.store.aladdin.exceptions;

public class ShippingTokenException extends RuntimeException {

    public ShippingTokenException(String message) {
        super(message);
    }

    public ShippingTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
