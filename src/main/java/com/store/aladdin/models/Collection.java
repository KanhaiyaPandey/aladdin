package com.store.aladdin.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Document(collection = "collections")
@NoArgsConstructor
public class Collection {

    @Id
    private String collectionId;

    @NonNull
    private String name;
    
    private String description;
    
    private String image;
    
    private String type; // e.g. "Manual" or "Automatic"
    
    private boolean featured;

}
