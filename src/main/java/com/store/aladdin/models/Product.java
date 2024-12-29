package com.store.aladdin.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String productId;

    @NonNull
    private String name;

    private String description;

    private Double price;

    private Integer quantity;

    private StockStatus stockStatus;

    private List<String> options = new ArrayList<>();

    @Field("images")
    private List<String> images = new ArrayList<>(); 

    private List<Variant> variants = new ArrayList<>(); 

    public enum StockStatus {
        OUT_OF_STOCK,
        IN_STOCK,
        LIMITED_STOCK
    }

    private LocalDateTime date;

    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    public static class Variant {

        @Id
        private String variantId;
        
        private String parentProductId;
        private String color;
        private String size;
        private Double additionalPrice;
        private List<String> medias;
    }
}
