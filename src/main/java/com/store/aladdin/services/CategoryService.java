package com.store.aladdin.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Category;
import com.store.aladdin.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Find category by ID
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    // Find category by title
    public Category getCategoryByTitle(String title) {
        return categoryRepository.findByTitle(title);
    }

        // Get all categories
        public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    // Save a new category
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
}
