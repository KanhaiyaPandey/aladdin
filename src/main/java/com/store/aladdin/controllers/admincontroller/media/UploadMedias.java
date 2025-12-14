package com.store.aladdin.controllers.admincontroller.media;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.store.aladdin.routes.MediaRoutes;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.models.Medias;
import com.store.aladdin.services.ImageUploadService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

import static com.store.aladdin.routes.PublicRoutes.PUBLIC_BASE;

@RestController
@RequestMapping(PUBLIC_BASE)
@RequiredArgsConstructor
public class UploadMedias {


    private final ImageUploadService imageUploadService;

    private final MongoTemplate mongoTemplate;

    @PostMapping(value = "/media/upload-media", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadMultipleMedia(@RequestPart("media") MultipartFile[] files) {
        try {
            List<CompletableFuture<Medias>> futures = new ArrayList<>();

            for (MultipartFile file : files) {
                CompletableFuture<Medias> future = imageUploadService.uploadImageAsync(file)
                        .thenApply(imageUrl -> {
                            Medias media = Medias.builder()
                                    .url(imageUrl)
                                    .title(file.getOriginalFilename())
                                    .fileType(file.getContentType())
                                    .fileSize(file.getSize())
                                    .createdAt(LocalDateTime.now())
                                    .build();

                            mongoTemplate.save(media);
                            return media;
                        });
                futures.add(future);
            }

            List<Medias> uploadedMedias = futures.stream()
                    .map(CompletableFuture::join)  // join waits for each future
                    .toList();

            return ResponseUtil.buildResponse("images uploaded", true, uploadedMedias, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error uploading images:", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


        
    }
