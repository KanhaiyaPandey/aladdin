package com.store.aladdin.repository;

import com.store.aladdin.models.Attribute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttributesRepository extends MongoRepository<Attribute, String> {
    boolean existsByName(String name);
}
