package com.store.aladdin.controllers.AdminController.product;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DeleteProduct {
    
    private final ProductService productService;
  
    @DeleteMapping("/product/delete-product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(new ObjectId(productId)); 
        return ResponseUtil.buildResponse("Product deleted successfully", HttpStatus.OK);
    }


}
