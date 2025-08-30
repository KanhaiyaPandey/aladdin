package com.store.aladdin.controllers.admincontroller.product;

import java.util.Map;
import java.util.UUID;


import com.store.aladdin.routes.admin_routes.AdminProductRoutes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.store.aladdin.models.Product;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.helper.ProductHelper;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AdminProductRoutes.PRODUCT_BASE)
@RequiredArgsConstructor
public class CreateProduct {

    private final ProductService productService;
    private final ProductHelper productHelper;
    private final CategoryService categoryService;


    @PostMapping(value = AdminProductRoutes.CREATE_PRODUCT, consumes = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
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
