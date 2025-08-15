package com.store.aladdin.controllers.AdminController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.models.Medias;
import com.store.aladdin.services.ImageUploadService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UploadMedias {


    private final ImageUploadService imageUploadService;

    private final MongoTemplate mongoTemplate;

    @PostMapping(value = "/media/upload-media", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadMultipleMedia(@RequestParam("media") MultipartFile[] files) {
    try {
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
        }
            return ResponseUtil.buildResponse("image uploaded", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error uploading images:", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
}


        
    }
