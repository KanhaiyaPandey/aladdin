package com.store.aladdin.dtos;


import java.util.List;


import lombok.Data;

@Data
public class CategoryResponse {

    private String categoryId;
    private String title;
    private String description;
    private String banner;
    private String parentCategoryId;
    private String slug;
    private List<CategoryResponse> subCategories;
    private List<String>path;
    
    
}
