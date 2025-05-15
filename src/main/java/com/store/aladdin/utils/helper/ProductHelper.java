package com.store.aladdin.utils.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;
import com.store.aladdin.services.ImageUploadService;

@Service
public class ProductHelper {

    @Autowired
    private ProductQueries productQueries;

 public void validateProduct(Product product) {
        if (product.getTitle() == null || product.getTitle().isEmpty()) {
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
                    throw new RuntimeException("Failed to upload image: " + image.getOriginalFilename(), e);
                }
            }
        }
        return imageUrls;
    }

    // public void processVariantMedia(Product product, List<MultipartFile> variantMedias, ImageUploadService imageUploadService) {
    //     if (product.getVariants() != null && !product.getVariants().isEmpty()) {
    //         Map<String, List<String>> variantMediaUrlsMap = new HashMap<>();
    //         if (variantMedias != null && !variantMedias.isEmpty()) {
    //             for (MultipartFile media : variantMedias) {
    //                 try {
    //                     String mediaUrl = imageUploadService.uploadImage(media);
    //                     String variantId = extractVariantIdFromMedia(media);
    //                     variantMediaUrlsMap.computeIfAbsent(variantId, k -> new ArrayList<>()).add(mediaUrl);
    //                 } catch (IOException e) {
    //                     throw new RuntimeException("Failed to upload media: " + media.getOriginalFilename(), e);
    //                 }
    //             }
    //         }
    //         for (Product.Variant variant : product.getVariants()) {
    //             variant.setMedias(variantMediaUrlsMap.getOrDefault(variant.getVariantId(), new ArrayList<>()));
    //         }
    //     }
    // }

    @SuppressWarnings("unused")
    private String extractVariantIdFromMedia(MultipartFile media) {
        return "default-variant-id"; // Replace this with actual logic to extract variant ID from media
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
}
