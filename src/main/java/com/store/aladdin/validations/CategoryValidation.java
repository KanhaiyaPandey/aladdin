package com.store.aladdin.validations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Category;

@Service
public class CategoryValidation {

    @Autowired
    private MongoTemplate mongoTemplate;

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
        if (exists) {
            throw new IllegalArgumentException("Category with this title already exists");
        }


    }
    
}
