package com.store.aladdin.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.store.aladdin.models.Product;

public interface ProductRepository extends MongoRepository<Product, ObjectId>, CustomProductRepository{

    
    
}


