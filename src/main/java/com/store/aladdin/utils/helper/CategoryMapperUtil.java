package com.store.aladdin.utils.helper;

import java.util.*;
import java.util.stream.Collectors;

import com.store.aladdin.dtos.CategoryResponse;
import com.store.aladdin.models.Category;

public class CategoryMapperUtil {

    private CategoryMapperUtil(){
        throw new UnsupportedOperationException("Utility class");
    }

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
        response.setPath(category.getPath());
        response.setSlug(category.getSlug());
        response.setParentCategoryId(category.getParentCategoryId()); // ✅ fixed

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
