package com.store.aladdin.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> createProduct(
    @RequestParam("product") String productJson,
    @RequestPart(value = "images", required = false) List<MultipartFile> images,
    @RequestPart(value = "variantMedias", required = false) List<MultipartFile> variantMedias) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);

        validateProduct(product);
        product.setImages(uploadImages(images));
        processVariantMedia(product, variantMedias);

        // Ensure each variant's productId matches the product's id
      
            for (Product.Variant variant : product.getVariants()) {
                if (variant.getVariantId() == null || variant.getVariantId().isEmpty()) {
                    variant.setVariantId(UUID.randomUUID().toString());
                }
            }


        product.setCreatedAt(LocalDateTime.now());
        productService.createProduct(product);
        return ResponseUtil.buildResponse("Product created successfully", HttpStatus.OK);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading images: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
}

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
    }
    
    private List<String> uploadImages(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
            try {
                imageUrls.add(imageUploadService.uploadImage(image));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + image.getOriginalFilename(), e);
            }
            }
        }
        return imageUrls;
    }
    
    private void processVariantMedia(Product product, List<MultipartFile> variantMedias) {
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            Map<String, List<String>> variantMediaUrlsMap = new HashMap<>();
            if (variantMedias != null && !variantMedias.isEmpty()) {
                for (MultipartFile media : variantMedias) {
                    try{
                       String mediaUrl = imageUploadService.uploadImage(media);
                       String variantId = extractVariantIdFromMedia(media);
                       variantMediaUrlsMap.computeIfAbsent(variantId, k -> new ArrayList<>()).add(mediaUrl);
                    }catch (IOException e) {

                       throw new RuntimeException("Failed to upload image: " + media.getOriginalFilename(), e);


                    }
                }
            }
            for (Product.Variant variant : product.getVariants()) {
                variant.setMedias(variantMediaUrlsMap.getOrDefault(variant.getVariantId(), new ArrayList<>()));
            }
        }
    }
    
private String extractVariantIdFromMedia(MultipartFile media) {
    return "default-variant-id"; 
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