package com.store.aladdin.queries;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.store.aladdin.models.User;

@Component
public class UserRepositoryImpl {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUserForSA(){
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("kanhaiya"));
        List <User> users = mongoTemplate.find(query, User.class);
        return users;
    }

}
