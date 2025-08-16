package com.store.aladdin.validations;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryValidation {

    private final MongoTemplate mongoTemplate;

    public void validateCategory(Category category) {
        if (category.getTitle() == null || category.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (category.getBanner() == null) {
            throw new IllegalArgumentException("Banner required");
        }

        if (category.getDescription() == null || category.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("title").regex("^" + category.getTitle() + "$", "i"));

        // Exclude current category if updating
        if (category.getCategoryId() != null) {
            query.addCriteria(Criteria.where("_id").ne(category.getCategoryId()));
        }

        boolean exists = mongoTemplate.exists(query, Category.class);
        if (exists && category.getParentCategoryId().isEmpty()) {
            throw new IllegalArgumentException("Category with this title already exists");
        }


    }


    public void checkSubCategoryName(String title, String parentId) {
        // Step 1: Find the parent category by ID
        Category parentCategory = mongoTemplate.findOne(
            Query.query(Criteria.where("_id").is(new ObjectId(parentId))),
            Category.class
        );

        if (parentCategory == null) {
            throw new IllegalArgumentException("Parent category not found.");
        }

        // Step 2: Loop through childCategoryIds and fetch each subcategory
        List<String> childIds = parentCategory.getChildCategoryIds(); // assuming List<String>
        if (childIds == null || childIds.isEmpty()) return;

        // Step 3: Find all child categories in a single query
       List<Category> subCategories = mongoTemplate.find(
            Query.query(Criteria.where("_id").in(
                childIds.stream().map(ObjectId::new).toList()
            )),
            Category.class
        );

        for (Category sub : subCategories) {
            if (sub.getTitle().equalsIgnoreCase(title)) {
                throw new IllegalArgumentException(
                    "Sub Category with title \"" + title + "\" already exists under the category \"" 
                    + parentCategory.getTitle() + "\""
                );
            }
        }
    }
        
}
