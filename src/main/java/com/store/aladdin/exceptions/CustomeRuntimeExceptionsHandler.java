package com.store.aladdin.exceptions;

public class CustomeRuntimeExceptionsHandler extends RuntimeException {

    public CustomeRuntimeExceptionsHandler(String message){
          super(message);
    }
    public CustomeRuntimeExceptionsHandler(String message, Throwable cause){
          super(message, cause);
    }
    
}
