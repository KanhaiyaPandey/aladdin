package com.store.aladdin.utils.helper;

import java.util.*;
import java.util.stream.Collectors;

import com.store.aladdin.DTOs.CategoryResponse;
import com.store.aladdin.models.Category;

public class CategoryMapperUtil {

    // Public entry point
    public static CategoryResponse mapToCategoryResponse(Category category, Map<String, Category> categoryMap) {
        return mapToCategoryResponse(category, categoryMap, new HashSet<>());
    }

    // Private recursive method with cycle detection
    private static CategoryResponse mapToCategoryResponse(Category category, Map<String, Category> categoryMap, Set<String> visited) {
        if (category == null) return null;

        String categoryId = category.getCategoryId();

        // Prevent infinite recursion
        if (visited.contains(categoryId)) {
            return null;
        }

        visited.add(categoryId);

        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(categoryId);
        response.setTitle(category.getTitle());
        response.setDescription(category.getDescription());
        response.setBanner(category.getBanner());

        // If products are stored as Product objects, convert them to string IDs
        if (category.getCategoryProducts() != null) {
            response.setCategoryProducts(category.getCategoryProducts());
        }

        // Set parentCategory
        if (category.getParentCategoryId() != null) {
            Category parent = categoryMap.get(category.getParentCategoryId());
            response.setParentCategory(mapToCategoryResponse(parent, categoryMap, visited));
        }

        // Set subCategories
        if (category.getChildCategoryIds() != null && !category.getChildCategoryIds().isEmpty()) {
            List<CategoryResponse> subCategories = category.getChildCategoryIds().stream()
                .map(id -> mapToCategoryResponse(categoryMap.get(id), categoryMap, visited))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            response.setSubCategories(subCategories);
        }

        visited.remove(categoryId); // Backtrack

        return response;
    }
}
