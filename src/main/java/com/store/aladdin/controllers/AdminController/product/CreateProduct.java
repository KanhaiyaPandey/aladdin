package com.store.aladdin.controllers.AdminController.product;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Product;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ProductService;
import com.store.aladdin.utils.ResponseUtil;
import com.store.aladdin.utils.helper.ProductHelper;

@RestController
@RequestMapping("/api/admin")
public class CreateProduct {


    @Autowired
    private ProductService productService;


    @Autowired
    private ProductHelper productHelper;

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping(value = "/create-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(
            @RequestParam("product") String productJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Product product = objectMapper.readValue(productJson, Product.class);

            productHelper.validateProduct(product);
            // product.setImages(productHelper.uploadImages(images, imageUploadService));
            // productHelper.processVariantMedia(product, variantMedias, imageUploadService);

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

            return ResponseUtil.buildResponse("Product created successfully", true ,proUp, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading images: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    
}
