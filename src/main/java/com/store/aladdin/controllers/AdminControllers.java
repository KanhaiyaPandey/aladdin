package com.store.aladdin.controllers;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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




    
    @PostMapping(value = "/create-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
        @RequestPart("product") @Validated Product product,
        @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            // Upload images to Cloudinary
            System.out.println("Received product: " + product);
            System.out.println("Received images: " + (images != null ? images.size() : "No images"));
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageUrl = imageUploadService.uploadImage(image);
                imageUrls.add(imageUrl);
            }

            // Set image URLs in the product
            product.setImages(imageUrls);

            // Save the product
            productService.createProduct(product);

            return ResponseEntity.ok(ResponseUtil.buildResponse("Product created successfully", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product creation failed: " + e.getMessage());
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