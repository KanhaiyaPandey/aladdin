package com.store.aladdin.utils.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    private static final String STATUS_KEY = "status";
    private static final String MESSAGE_KEY = "message";
    private static final String SUCCESS_KEY = "success";
    private static final String DATA_KEY = "data";
    private static final String ERROR_KEY = "error";
    private static final String DETAILS_KEY = "details";

    // Method to build a response with a message and HTTP status
    public static ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, message);
        response.put(STATUS_KEY, status.value());
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<Map<String, Object>> buildResponse(String message, Boolean success, Object data, HttpStatus status){
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE_KEY, message);
        response.put(SUCCESS_KEY, success);
        response.put(DATA_KEY, data);
         return new ResponseEntity<>(response, status);
    }

    // Method to build an error response with a message and status
    public static ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR_KEY, message);
        response.put(STATUS_KEY, status.value());
        return new ResponseEntity<>(response, status);
    }



    // Method to build an error response with message, status, and details
    public static ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR_KEY, message);
        response.put(STATUS_KEY, status.value());
        response.put(DETAILS_KEY, details);
        return new ResponseEntity<>(response, status);
    }

    
}
