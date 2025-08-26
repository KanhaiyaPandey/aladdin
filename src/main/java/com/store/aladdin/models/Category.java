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
    private String banner;
    private String parentCategoryId;
    private List<String> childCategoryIds = new ArrayList<>();
    private List<String> path = new ArrayList<>();
    
}
