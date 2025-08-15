package com.store.aladdin.controllers.AdminController.product;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Product;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.helper.ProductHelper;
import com.store.aladdin.utils.response.ResponseUtil;
import com.store.aladdin.utils.validation.ValidationException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class CreateProduct {

    private final ProductService productService;
    private final ProductHelper productHelper;
    private final CategoryService categoryService;

    
    @PostMapping(value = "/create-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestParam("product") String productJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.readValue(productJson, Product.class);

            productHelper.validateProduct(product);
            for (Product.Variant variant : product.getVariants()) {
                if (variant.getVariantId() == null || variant.getVariantId().isEmpty()) {
                    variant.setVariantId(UUID.randomUUID().toString());
                }
            }

            product.setCreatedAt(LocalDateTime.now());
            Product pro = productService.createProduct(product);

            ObjectId objectId = new ObjectId(pro.getProductId());
            Product proUp = productService.updateProductVariants(objectId, product);

            List<String> categoryIds = product.getProductCategories().stream()
            .map(Product.ProductCategories::getCategoryId)
            .toList();

            if (categoryIds != null && !categoryIds.isEmpty()) {
                categoryService.addProductToCategories(pro, categoryIds);
            }

            return ResponseUtil.buildResponse("Product created successfully", true, proUp, HttpStatus.OK);
            
            } catch (ValidationException ve) {
                return ResponseUtil.buildErrorResponse("Validation error", HttpStatus.BAD_REQUEST, ve.getMessage());
            } catch (IOException e) {
                return ResponseUtil.buildErrorResponse("Error uploading images", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Exception e) {
                return ResponseUtil.buildErrorResponse("Error", HttpStatus.BAD_REQUEST, e.getMessage());
            }
    }

    
}
