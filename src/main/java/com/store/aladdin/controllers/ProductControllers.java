package com.store.aladdin.controllers;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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



    @DeleteMapping("delete-product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(new ObjectId(productId)); 
        return ResponseUtil.buildResponse("Product deleted successfully", HttpStatus.OK);
    }

    @GetMapping("get-product/{productId}")
public ResponseEntity<?> getProductById(@PathVariable String productId) {
    try {
        Product product = productService.getProductById(new ObjectId(productId));
        return ResponseUtil.buildResponse(product, HttpStatus.OK);
    } catch (Exception e) {
        return ResponseUtil.buildResponse("Product not found", HttpStatus.NOT_FOUND);
    }
}




}
