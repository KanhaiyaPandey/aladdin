package com.store.aladdin.controllers.AdminController;

import java.io.IOException;
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
import com.store.aladdin.utils.helper.ProductHelper;

@RestController
@RequestMapping("/api/admin")
public class CreateCategory {

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductHelper productHalper;



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
            List<String> bannerUrls = productHalper.uploadImages(bannerImages, imageUploadService);
    
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
    
}
