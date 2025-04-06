package com.store.aladdin.controllers.AdminController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.models.Medias;
import com.store.aladdin.services.ImageUploadService;

@RestController
@RequestMapping("/api/admin")
public class UploadMedias {

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping(value = "/media/upload-media", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> uploadMultipleMedia(@RequestParam("media") MultipartFile[] files) {
    try {
        List<Medias> uploadedMedias = new ArrayList<>();

        for (MultipartFile file : files) {
            String imageUrl = imageUploadService.uploadImage(file);
            Medias media = Medias.builder()
                .url(imageUrl)
                .title(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .createdAt(LocalDateTime.now())
                .build();

            mongoTemplate.save(media);
            uploadedMedias.add(media);
        }

            return ResponseEntity.ok().body(uploadedMedias); 
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        }
}


        
    }
