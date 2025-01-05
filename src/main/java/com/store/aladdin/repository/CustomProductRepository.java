package com.store.aladdin.repository;

import java.util.List;

import com.store.aladdin.models.Product;

public interface CustomProductRepository {
        List<Product> findFilteredProducts(String name, Double minPrice, Double maxPrice, String stockStatus);

}
