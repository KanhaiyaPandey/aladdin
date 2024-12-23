package com.store.aladdin.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Product;
import com.store.aladdin.models.User;
import com.store.aladdin.services.ImageUploadService;
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

    @Autowired
    private ImageUploadService imageUploadService;


@PostMapping(value = "/create-product", consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
    @RequestParam("product") String productJson,
    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
    try {
        // Deserialize the JSON string into a Product object
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);

        // System.out.println("Received product: " + product);
        // System.out.println("Received images: " + (images != null ? images.size() : "No images"));

        // Handle image upload and set URLs in the product
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrl = imageUploadService.uploadImage(image);
                imageUrls.add(imageUrl);
            }
        }
        product.setImages(imageUrls);
        product.setDate(LocalDateTime.now());
        productService.createProduct(product);

        return ResponseUtil.buildResponse("Product updated successfully", HttpStatus.OK);
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
}





    // update product

     @PutMapping("/product/update-product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable ObjectId productId, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(productId, product);
        return ResponseUtil.buildResponse("Product updated successfully", HttpStatus.OK, updatedProduct);
    }

    
    @DeleteMapping("/product/delete-product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(new ObjectId(productId)); 
        return ResponseUtil.buildResponse("Product deleted successfully", HttpStatus.OK);
    }



        // Get all users
        @GetMapping("/users/all-users")
        public ResponseEntity<List<User>> getAllUsers() {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
    }