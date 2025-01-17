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
    private String title;

    private String description;

    private Double costPrice;
    private Double sellPrice;
    private Double compareAtPrice;

    private boolean allowBackorder;

    private StockStatus stockStatus;

    private List<String> attributes = new ArrayList<>();

    private List<String> productCategories = new ArrayList<>();

    @Field("images")
    private List<String> images = new ArrayList<>(); 

    private List<Variant> variants = new ArrayList<>(); 

    private List<Warehouse> warehouseData = new ArrayList<>(); 

    private List<Dimension> dimensions = new ArrayList<>(); 

    public enum StockStatus {
        OUT_OF_STOCK,
        IN_STOCK,
        LIMITED_STOCK
    }

    private LocalDateTime date;

    private LocalDateTime createdAt;




    // other constructors



    @Data
    @NoArgsConstructor
    public static class Variant {

        private String variantId;   
        private String parentProductId;
        private List<String> options = new ArrayList<>();
        private List<String> medias;
        private Double costPrice;
        private Double sellPrice;
        private Double compareAtPrice;
        private List<Warehouse> variantWarehouseData = new ArrayList<>();
  
    }


    @Data
    @NoArgsConstructor
    public static class Dimension {

      private double length;
      private double height;
      private double width;
      private double weight;
    }


    @Data
    @NoArgsConstructor
    public static class Warehouse {
      
        private String warehouseId;
        private String name;
        private String location;
        private Integer stock;
    }
}



