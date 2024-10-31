package com.store.aladdin.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Document(collection = "products")
@NoArgsConstructor
public class Product {
    
    @Id
    private String id;

    @NonNull
    private String name;

    private String description;

    private Double price;
    
    private Integer quantity;

    private LocalDateTime  date;
}
