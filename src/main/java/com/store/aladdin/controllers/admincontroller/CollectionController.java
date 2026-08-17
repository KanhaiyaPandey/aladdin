package com.store.aladdin.controllers.admincontroller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.models.Collection;
import com.store.aladdin.services.CollectionService;
import com.store.aladdin.services.ImageUploadService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/aladdin/admin/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;
    private final ImageUploadService imageUploadService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCollection(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("type") String type,
            @RequestParam("featured") boolean featured,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        
        try {
            Collection collection = new Collection();
            collection.setName(name);
            collection.setDescription(description);
            collection.setType(type);
            collection.setFeatured(featured);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(imageFile);
                collection.setImage(imageUrl);
            }

            Collection savedCollection = collectionService.createCollection(collection);
            return ResponseUtil.buildResponse("Collection created successfully", true, savedCollection, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseUtil.buildErrorResponse("Error uploading image", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error creating collection", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCollections() {
        try {
            List<Collection> collections = collectionService.getAllCollections();
            return ResponseUtil.buildResponse("Collections fetched successfully", true, collections, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error fetching collections", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCollection(@PathVariable String id) {
        try {
            collectionService.deleteCollection(id);
            return ResponseUtil.buildResponse("Collection deleted successfully", true, null, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error deleting collection", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // Toggle featured status
    @PutMapping("/{id}/featured")
    public ResponseEntity<Map<String, Object>> toggleFeatured(@PathVariable String id, @RequestParam("featured") boolean featured) {
        try {
            Collection collection = collectionService.getAllCollections().stream().filter(c -> c.getCollectionId().equals(id)).findFirst().orElseThrow();
            collection.setFeatured(featured);
            Collection updated = collectionService.updateCollection(id, collection);
            return ResponseUtil.buildResponse("Collection featured status updated", true, updated, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error updating collection", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
