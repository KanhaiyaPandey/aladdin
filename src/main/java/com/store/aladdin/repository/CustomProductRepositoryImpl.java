package com.store.aladdin.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.store.aladdin.models.Product;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {


    private final MongoTemplate mongoTemplate;

    @Override
    public List<Product> findFilteredProducts(String name, Double minPrice, Double maxPrice, String stockStatus) {
        Query query = new Query();

        if (name != null && !name.isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(name, "i")); // Case-insensitive search
        }
        if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        }
        if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }
        if (stockStatus != null && !stockStatus.isEmpty()) {
            query.addCriteria(Criteria.where("stockStatus").is(stockStatus));
        }

        return mongoTemplate.find(query, Product.class);
    }
}
