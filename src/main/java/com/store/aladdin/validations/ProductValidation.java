package com.store.aladdin.validations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;

@Component
public class ProductValidation {

    @Autowired
    private ProductQueries productQueries;
    
    public void validateProduct(Product product) {
        if (product.getTitle() == null) {
            throw new IllegalArgumentException("Product name is required.");
        }

        if (isNullOrEmpty(product.getSku())) {
            throw new IllegalArgumentException("Product SKU is required.");
        }

        if (productQueries.doesSkuExist(product.getSku())) {
            throw new IllegalArgumentException("Product SKU must be unique. SKU already exists: " + product.getSku());
        }

    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}
