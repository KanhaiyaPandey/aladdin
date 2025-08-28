package com.store.aladdin.repository;

import com.store.aladdin.models.User;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}

