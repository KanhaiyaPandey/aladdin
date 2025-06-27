package com.store.aladdin.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.aladdin.DTOs.CategoryResponse;
import com.store.aladdin.models.Category;
import com.store.aladdin.models.Product;
import com.store.aladdin.repository.CategoryRepository;
import com.store.aladdin.utils.helper.CategoryMapperUtil;

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





    public List<CategoryResponse> getAllCategoryResponses() {
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Category> categoryMap = allCategories.stream()
        .collect(Collectors.toMap(cat -> cat.getCategoryId().toString(), cat -> cat));
        return allCategories.stream()
            .filter(cat -> cat.getParentCategoryId() == null)
            .map(cat -> CategoryMapperUtil.mapToCategoryResponse(cat, categoryMap))
            .collect(Collectors.toList());
    }







    // Save a new category
    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        if (category.getParentCategoryId() != null) {
            ObjectId parentId = new ObjectId(category.getParentCategoryId());
            Optional<Category> parentCategoryOpt = categoryRepository.findById(parentId);

            if (parentCategoryOpt.isPresent()) {
                Category parentCategory = parentCategoryOpt.get();
                if (parentCategory.getChildCategoryIds() == null) {
                    parentCategory.setChildCategoryIds(new ArrayList<>());
                }
                parentCategory.getChildCategoryIds().add(savedCategory.getCategoryId());
                categoryRepository.save(parentCategory);
            } else {
                throw new RuntimeException("Parent category not found with ID: " + category.getParentCategoryId());
            }
        }
        return savedCategory;
    }




    // Add a product to the specified categories
    public void addProductToCategories(Product product, List<String> categoryIds) {
        List<ObjectId> objectIds = categoryIds.stream()
                                            .map(ObjectId::new)
                                            .toList();
        List<Category> categories = categoryRepository.findAllById(objectIds);
        for (Category category : categories) {
            // Add product to the category's product list if not already present
            if (!category.getCategoryProducts().contains(product)) {
                category.getCategoryProducts().add(product);
            }
        }
        categoryRepository.saveAll(categories);
    }


}
