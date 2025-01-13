package com.store.aladdin.services;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Category;
import com.store.aladdin.models.Product;
import com.store.aladdin.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Find category by ID
    public Optional<Category> getCategoryById(ObjectId id) {
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

        // Add a product to the specified categories
public void addProductToCategories(Product product, List<String> categoryIds) {
    // Convert String category IDs to ObjectId
    List<ObjectId> objectIds = categoryIds.stream()
                                          .map(ObjectId::new)
                                          .toList();

    // Find all categories by ObjectId
    List<Category> categories = categoryRepository.findAllById(objectIds);

    for (Category category : categories) {
        // Add product to the category's product list if not already present
        if (!category.getCategoryProducts().contains(product)) {
            category.getCategoryProducts().add(product);
        }
    }

    // Save updated categories
    categoryRepository.saveAll(categories);
}


}
