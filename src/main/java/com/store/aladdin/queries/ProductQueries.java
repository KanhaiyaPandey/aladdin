package com.store.aladdin.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.store.aladdin.models.Product;

import java.util.List;

@Component
public class ProductQueries {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Product> filteredProducts(String name, Double minPrice, Double maxPrice, String stockStatus) {
        Query query = new Query();
        List<Criteria> criteriaList = new java.util.ArrayList<>();
    
        if (name != null && !name.isEmpty()) {
            criteriaList.add(Criteria.where("name").regex(".*" + name + ".*", "i"));
        }
    
        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = Criteria.where("sellPrice");
            if (minPrice != null) {
                priceCriteria = priceCriteria.gte(minPrice);
            }
            if (maxPrice != null) {
                priceCriteria = priceCriteria.lte(maxPrice);
            }
            criteriaList.add(priceCriteria);
        }
    
        if (stockStatus != null && !stockStatus.isEmpty()) {
            // boolean inStock = stockStatus.equals("IN_STOCK");
            criteriaList.add(Criteria.where("stockStatus").is("IN_STOCK"));
        }
    
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
    
        return mongoTemplate.find(query, Product.class);
    }
    
}
