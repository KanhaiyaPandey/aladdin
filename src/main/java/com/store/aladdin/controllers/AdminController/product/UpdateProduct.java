package com.store.aladdin.controllers.AdminController.product;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.aladdin.models.Product;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/api/admin")
public class UpdateProduct {
    

    @Autowired
    private ProductService productService;

    
    @PutMapping("/product/update-product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable ObjectId productId, @RequestBody String productJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            Product product = objectMapper.readValue(productJson, Product.class);
            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseUtil.buildResponse("Product updated successfully", true ,updatedProduct,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Failed to update product", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    
}
