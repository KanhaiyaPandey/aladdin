package com.store.aladdin.controllers;

import java.util.List;

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
@RequestMapping("/api")
public class TestingControllers {


@PostMapping(value = "/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> testEndpoint(
    @RequestPart("product") String product,
    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
    return ResponseUtil.buildResponse("Test successfull", HttpStatus.OK);
}
    
}
