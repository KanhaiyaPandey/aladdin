package com.store.aladdin.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/health")
public class TestingControllers {


    @GetMapping(value = "/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        return ResponseUtil.buildResponse("✅ everything is healthy v2", HttpStatus.OK);
    }
    
}
