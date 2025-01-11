package com.store.aladdin.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Category;
import com.store.aladdin.services.CategoryService;



@RestController
@CrossOrigin(origins = "http://localhost:5174")
@RequestMapping("/api/public")
public class CategoryControllers {

       @Autowired
    private CategoryService categoryService;

    // Endpoint to fetch all categories
    @GetMapping("/category/all-categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No categories found");
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching categories: " + e.getMessage());
        }
    }
    
}
