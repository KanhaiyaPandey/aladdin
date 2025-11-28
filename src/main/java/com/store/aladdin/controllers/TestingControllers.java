package com.store.aladdin.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/health")
public class TestingControllers {


    @PostMapping(value = "/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        return ResponseUtil.buildResponse("✅ everything is healthy", HttpStatus.OK);
    }
    
}
