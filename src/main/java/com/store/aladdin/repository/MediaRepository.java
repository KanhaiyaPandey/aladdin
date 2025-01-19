package com.store.aladdin.repository;


import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.store.aladdin.models.Medias;


public interface MediaRepository extends MongoRepository<Medias, ObjectId>{
    
}
