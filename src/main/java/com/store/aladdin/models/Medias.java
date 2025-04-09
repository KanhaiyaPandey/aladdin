package com.store.aladdin.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medias")
public class Medias {

    @Id
    private String mediaId;

    private String url; 
    private String title; 
    private String fileType; 
    private long fileSize; 
    private LocalDateTime createdAt; 
}
