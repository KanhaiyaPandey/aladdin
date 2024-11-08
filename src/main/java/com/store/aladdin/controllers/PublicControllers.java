package com.store.aladdin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Product;
import com.store.aladdin.models.User;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/public")
public class PublicControllers {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

     @GetMapping("/all-products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        userService.createUser(user);
        return ResponseUtil.buildResponse("User created successfully", HttpStatus.CREATED);
    }
    
}
