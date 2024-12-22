package com.store.aladdin.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class TestingControllers {


@PostMapping(value = "/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> testEndpoint(
    @RequestPart("product") String product,
    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
    System.out.println("Product: " + product);
    System.out.println("Images: " + (images != null ? images.size() : "No images"));
    return ResponseEntity.ok("Test successful");
}
    
}
