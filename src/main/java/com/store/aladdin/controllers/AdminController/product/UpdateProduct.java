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
    public ResponseEntity<?> updateProduct(@PathVariable ObjectId productId, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseUtil.buildResponse("Product updated successfully", true ,updatedProduct,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Failed to update product", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    
}
