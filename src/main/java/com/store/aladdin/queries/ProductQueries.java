package com.store.aladdin.queries;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.store.aladdin.models.Product;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ProductQueries {

    private final MongoTemplate mongoTemplate;

    public List<Product> filteredProducts(
            String name,
            Double minPrice,
            Double maxPrice,
            String stockStatus,
            String category,
            String collectionSlug
    ) {
        Query query = new Query();
        List<Criteria> criteriaList = new java.util.ArrayList<>();

        // 🔹 Filter by product name (title or tags)
        if (name != null && !name.isEmpty()) {
            String escaped = Pattern.quote(name);
            Criteria tagsCriteria = Criteria.where("tags").regex(".*" + escaped + ".*", "i");
            Criteria titleCriteria = Criteria.where("title").regex(".*" + escaped + ".*", "i");
            criteriaList.add(new Criteria().orOperator(tagsCriteria, titleCriteria));
        }

        // 🔹 Filter by price range
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

        // 🔹 Filter by stock status
        if (stockStatus != null && !stockStatus.isEmpty()) {
            criteriaList.add(Criteria.where("stockStatus").is(stockStatus));
        }

        // 🔹 Filter by category slug (inside productCategories array)
        if (category != null && !category.isEmpty()) {
            criteriaList.add(Criteria.where("productCategories.slug").is(category));
        }

        // 🔹 Filter by collection slug (assuming `collections` array exists in Product model)
//        if (collectionSlug != null && !collectionSlug.isEmpty()) {
//            criteriaList.add(Criteria.where("collections.slug").is(collectionSlug));
//        }

        // Combine all filters
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Product.class);
    }

    // 🔹 Check if a SKU exists
    public boolean doesSkuExist(String sku) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sku").is(sku));
        return mongoTemplate.exists(query, Product.class);
    }
}
