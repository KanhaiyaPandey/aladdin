package com.store.aladdin.DTOs;

import java.util.ArrayList;
import java.util.List;

import com.store.aladdin.models.Product;

import lombok.Data;

@Data
public class CategoryResponse {

    private String categoryId;
    private String title;
    private String description;
    private List<String> banner = new ArrayList<>();
    private List<Product> categoryProducts = new ArrayList<>();
    private CategoryResponse parentCategory;
    private List<CategoryResponse> subCategories;
    
}
