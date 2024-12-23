package com.store.aladdin.controllers;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Product;
import com.store.aladdin.models.User;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.ResponseUtil;
import com.store.aladdin.utils.ValidationUtils;

@RestController
@RequestMapping("/api/public")
public class PublicControllers {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired 
    private BCryptPasswordEncoder passwordEncoder;

     @GetMapping("/product/all-products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        String validationMessage = ValidationUtils.validateUser(user);
        if (validationMessage != null) {
            return ResponseUtil.buildResponse(validationMessage, HttpStatus.BAD_REQUEST);
        }
    
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("USER"));
        }
    
        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            userService.createUser(user);
            return ResponseUtil.buildResponse("User created successfully", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseUtil.buildResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // user login 

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        User user = userService.getUserByEmail(loginUser.getEmail());
        if (user == null) {
            return ResponseUtil.buildResponse("User not found", HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            return ResponseUtil.buildResponse("Login successful", HttpStatus.OK);
        } else {
            return ResponseUtil.buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
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
