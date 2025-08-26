package com.store.aladdin.utils.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;
import com.store.aladdin.services.ImageUploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductHelper {

    private final ProductQueries productQueries;

 public void validateProduct(Product product) {
        if (product.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (isNullOrEmpty(product.getSku())) {
            throw new IllegalArgumentException("Product SKU is required.");
        }

        if (productQueries.doesSkuExist(product.getSku())) {
            throw new IllegalArgumentException("Product SKU must be unique. SKU already exists: " + product.getSku());
        }
    }

    public List<String> uploadImages(List<MultipartFile> images, ImageUploadService imageUploadService) {
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                try {
                    imageUrls.add(imageUploadService.uploadImage(image));
                } catch (IOException e) {
                    throw new CustomeRuntimeExceptionsHandler("Failed to upload image: " + image.getOriginalFilename(), e);
                }
            }
        }
        return imageUrls;
    }

    @SuppressWarnings("unused")
    private String extractVariantIdFromMedia(MultipartFile media) {
        return "default-variant-id"; // Replace this with actual logic to extract variant ID from media
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
}
