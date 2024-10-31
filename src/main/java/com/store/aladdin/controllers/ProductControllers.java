package com.store.aladdin.controllers;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Product;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/products")
public class ProductControllers {
    
    @Autowired
    private ProductService productService;

        @PostMapping("create-product")
        public ResponseEntity<?> createProduct(@RequestBody Product product) {
        productService.createProduct(product);
        return ResponseUtil.buildResponse("product created successfully", HttpStatus.OK);
    }

        @PutMapping("update-product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable ObjectId productId, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseUtil.buildResponse("Product updated successfully", HttpStatus.OK, updatedProduct);
    }

    @DeleteMapping("delete-product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(new ObjectId(productId)); 
        return ResponseUtil.buildResponse("Product deleted successfully", HttpStatus.OK);
    }



}
