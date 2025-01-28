package com.store.aladdin.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.store.aladdin.models.Order;


public interface OrderRepository extends MongoRepository <Order, ObjectId> {
    
}
