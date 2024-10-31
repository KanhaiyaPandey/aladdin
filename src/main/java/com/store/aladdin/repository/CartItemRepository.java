package com.store.aladdin.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.store.aladdin.utils.CartItem;

public interface CartItemRepository extends MongoRepository<CartItem, ObjectId> {
    
}
