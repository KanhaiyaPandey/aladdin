package com.store.aladdin.controllers;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Product;
import com.store.aladdin.models.User;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/admin")
public class AdminControllers {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

        // Get all users
    @GetMapping("/users/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    
        @PostMapping("/product/create-product")
        public ResponseEntity<?> createProduct(@RequestBody Product product) {
        productService.createProduct(product);
        return ResponseUtil.buildResponse("product created successfully", HttpStatus.OK);
    }

    // update product

     @PutMapping("update-product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable ObjectId productId, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseUtil.buildResponse("Product updated successfully", HttpStatus.OK, updatedProduct);
    }
}
