package com.store.aladdin.utils;

// import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<?> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<?> buildResponse(String message, HttpStatus status, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", status.value());
        response.put("data", data);
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<?> buildResponse(Object data, HttpStatus status) {
        return new ResponseEntity<>(data, status);
    }

    

}
