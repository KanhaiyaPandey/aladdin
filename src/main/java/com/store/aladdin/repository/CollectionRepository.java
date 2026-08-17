package com.store.aladdin.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.store.aladdin.models.Collection;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {
    List<Collection> findByFeaturedTrue();
}
