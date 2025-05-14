package com.store.aladdin.utils.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    // Method to build a response with a message and HTTP status
    public static ResponseEntity<?> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<?> buildResponse(String message, Boolean success, Object data, HttpStatus status){
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("success", success);
        response.put("data", data);
         return new ResponseEntity<>(response, status);
    }

    // Method to build an error response with a message and status
    public static ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }



    // Method to build an error response with message, status, and details
    public static ResponseEntity<?> buildErrorResponse(String message, HttpStatus status, Object details) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("status", status.value());
        response.put("details", details);
        return new ResponseEntity<>(response, status);
    }

    
}
