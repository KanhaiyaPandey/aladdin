package com.store.aladdin.controllers.admincontroller.product;

import java.util.Map;

import com.store.aladdin.routes.ProductRoutes;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ProductRoutes.PRODUCT_BASE)
@RequiredArgsConstructor
public class UpdateProduct {
    
    private final ProductService productService;

    
    @PutMapping(ProductRoutes.UPDATE_PRODUCT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable String productId, @RequestBody String productJson) {
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
