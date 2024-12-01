package com.store.aladdin.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    private StockStatus stockStatus;

    @Field("images")
    private List<String> images;

    public enum StockStatus {
        OUT_OF_STOCK,
        IN_STOCK,
        LIMITED_STOCK
    }

    private LocalDateTime date;
}
 