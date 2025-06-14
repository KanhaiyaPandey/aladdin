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

    private String description;

    private List<String> banner = new ArrayList<>(); 

    private List<Product> categoryProducts = new ArrayList<>();

    private String parentCategoryId;

    // 🔁 Optional: Children categories - populated manually or via custom query
    private List<ChildCategories> childCategoryIds = new ArrayList<>();

    public static class ChildCategories {
        private String categoryId;
        private String title;       
    }
    
}
