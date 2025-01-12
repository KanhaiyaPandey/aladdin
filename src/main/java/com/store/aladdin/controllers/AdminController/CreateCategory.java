package com.store.aladdin.controllers.AdminController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Category;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ImageUploadService;

@RestController
@RequestMapping("/api/admin")
public class CreateCategory {

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private CategoryService categoryService;



        @PostMapping(value = "/create-category", consumes = "multipart/form-data")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> createCategory(
        @RequestParam("category") String categoryJson,
        @RequestPart(value = "banner", required = false) List<MultipartFile> bannerImages) {
    
        try {
            // Parse JSON string into a Category object
            ObjectMapper objectMapper = new ObjectMapper();
            Category category = objectMapper.readValue(categoryJson, Category.class);
    
            // Upload banner images
            List<String> bannerUrls = uploadImages(bannerImages);
    
            // Set uploaded URLs to the category
            category.setBanner(bannerUrls);
    
            // Save the category using the service
            Category savedCategory = categoryService.createCategory(category);
    
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid category JSON format: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating category: " + e.getMessage());
        }
    }


    private List<String> uploadImages(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
            try {
                imageUrls.add(imageUploadService.uploadImage(image));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + image.getOriginalFilename(), e);
            }
            }
        }
        return imageUrls;
    }


    
}
