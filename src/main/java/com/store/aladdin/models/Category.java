package com.store.aladdin.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@Document(collection = "categories")
@NoArgsConstructor
public class Category {

    @Id
    private String categoryId;

    @NonNull
    private String title;

    private String slug;

    private String description;

    private List<String> banner = new ArrayList<>(); 

    private List<Product> categoryProducts = new ArrayList<>();

    private String parentCategoryId;
    
    private List<String> childCategoryIds = new ArrayList<>();
    
}
