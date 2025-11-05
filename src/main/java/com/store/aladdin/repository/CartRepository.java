package com.store.aladdin.repository;

import com.store.aladdin.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {
}
