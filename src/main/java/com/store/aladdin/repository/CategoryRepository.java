package com.store.aladdin.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.store.aladdin.models.Category;

@Repository
public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    
    Category findByTitle(String title);
    List<Category> findByParentCategoryId(String parentCategoryId);

}

