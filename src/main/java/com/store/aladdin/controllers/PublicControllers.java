package com.store.aladdin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Product;
import com.store.aladdin.services.ProductService;

@RestController
@RequestMapping("/api/public")
public class PublicControllers {
    
    @Autowired
    private ProductService productService;

     @GetMapping("/all-products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
}
