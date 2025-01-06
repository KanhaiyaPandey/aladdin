package com.store.aladdin.controllers;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Product;

import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.ResponseUtil;


@RestController
@RequestMapping("/api/public")
public class PublicControllers {
    
    @Autowired
    private ProductService productService;

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/product/all-products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String stockStatus) {
        try {
            List<Product> products = productService.getFilteredProducts(name, minPrice, maxPrice, stockStatus);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching products: " + e.getMessage());
        }
    }
    

    
    @GetMapping("product/{productId}")
public ResponseEntity<?> getProductById(@PathVariable String productId) {
    try {
        Product product = productService.getProductById(new ObjectId(productId));
        return ResponseUtil.buildResponse(product, HttpStatus.OK);
    } catch (Exception e) {
        return ResponseUtil.buildResponse("Product not found", HttpStatus.NOT_FOUND);
    }
}
    
}
