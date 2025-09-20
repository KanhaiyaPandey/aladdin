package com.store.aladdin.validations;

import org.springframework.stereotype.Component;

import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductValidation {

    private final ProductQueries productQueries;
    
    public void validateProduct(Product product) {

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
