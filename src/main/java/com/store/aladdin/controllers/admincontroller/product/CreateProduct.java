package com.store.aladdin.controllers.admincontroller.product;


import java.util.Map;
import java.util.UUID;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.routes.ProductRoutes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Product;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.helper.ProductHelper;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ProductRoutes.PRODUCT_BASE)
@RequiredArgsConstructor
public class CreateProduct {

    private final ProductService productService;
    private final ProductHelper productHelper;
    private final CategoryService categoryService;


    @PostMapping(value = ProductRoutes.CREATE_PRODUCT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody String reqJson) {
        Product product;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
             product = objectMapper.readValue(reqJson, Product.class);
        } catch (JsonProcessingException e) {
            throw new CustomeRuntimeExceptionsHandler("Something went wrong",e);
        }
        productHelper.validateProduct(product);
        for (Product.Variant variant : product.getVariants()) {
            if (variant.getVariantId() == null || variant.getVariantId().isEmpty()) {
                variant.setVariantId(UUID.randomUUID().toString());
            }
        }
        product.setSlug(generateSlug(product.getTitle()));
        Product createdProduct = productService.createProduct(product);
        Product updatedProduct = productService.updateProductVariants(createdProduct.getProductId(), product);
        return ResponseUtil.buildResponse("Product created successfully", true, updatedProduct, HttpStatus.OK
        );
    }


    private String generateSlug(String title) {
        return title.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }

    
}
