package com.store.aladdin.validations;

import org.springframework.stereotype.Service;

import com.store.aladdin.models.Category;

@Service
public class CategoryValidation {

    public void validateCategory(Category category) {
        if (category.getTitle() == null || category.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (category.getBanner() == null) {
            throw new IllegalArgumentException("Banner required");
        }

        if (category.getDescription() == null || category.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }
    
}
