package com.store.aladdin.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.store.aladdin.models.Store;

@Repository
public interface StoreRepository extends MongoRepository<Store, String> {
}
