package com.store.aladdin.controllers.public_controllers;

import java.util.List;
import java.util.Map;


import com.store.aladdin.services.RedisCacheService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.dtos.CategoryResponse;
import com.store.aladdin.models.Product;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicControllers {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final RedisCacheService redisCacheService;


    @GetMapping("/product/all-products")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String stockStatus) {
        try {
            List<Product> products = productService.getFilteredProducts(name, minPrice, maxPrice, stockStatus);
            return ResponseUtil.buildResponse("products fetched successfully", true, products, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error fetching products", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @GetMapping("product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String productId) {
        try {
            Product product = productService.getProductById(new ObjectId(productId));
            return ResponseUtil.buildResponse("product fetched successfully", true, product, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildResponse("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/category/all-categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        try {
            List<CategoryResponse> categories = categoryService.getAllCategoryResponses();
            if (categories.isEmpty()) {
                return ResponseUtil.buildResponse("No categories found", false, categories, HttpStatus.OK);
            }
            return ResponseUtil.buildResponse("categories fetched successfully", true, categories, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error fetching categories", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @GetMapping("/category/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable String id) {
        try {

            String cacheKey = "category:" + id;
            CategoryResponse cached = redisCacheService.get(cacheKey, CategoryResponse.class);
            if (cached != null) {
                return ResponseUtil.buildResponse("Category fetched from Redis", true, cached, HttpStatus.OK);
            }
            CategoryResponse category = categoryService.getCategoryById(new ObjectId(id));
            if (category == null) {
                return ResponseUtil.buildResponse("Category not found", false, null, HttpStatus.NOT_FOUND);
            }
            redisCacheService.set(cacheKey, category, 300L);
            return ResponseUtil.buildResponse("Category fetched successfully", true, category, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.buildErrorResponse("Invalid category ID format", HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error fetching categories", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

}
