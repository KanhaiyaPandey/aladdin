package com.store.aladdin.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.store.aladdin.utils.helper.Enums.Status;
import com.store.aladdin.utils.helper.Enums.StockStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Document(collection = "products")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {


    @Id
    private String productId;

    @NonNull
    private String title;
    private String slug;

    private String description;
    private Status status = Status.ACTIVE;

    private Double costPrice;
    private Double sellPrice;
    private Double compareAtPrice;

    private String sku;
    private boolean allowBackorder;
    private StockStatus stockStatus;
    private String barcode;
    private String gstNumber;
    private String sizeGuide;
    private List<String> attributes = new ArrayList<>();
    private List<ProductCategories> productCategories = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<String> crossSellProducts = new ArrayList<>();
    private List<String> upSellProducts = new ArrayList<>();

    private List<ProductMedia> productMedias = new ArrayList<>(); 

    private List<Variant> variants = new ArrayList<>(); 

    private List<Warehouse> warehouseData = new ArrayList<>(); 

    private List<Dimension> dimensions = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    // other constructors

    @Data
    @NoArgsConstructor
    public static class Variant {

        private String variantId;   
        private String parentProductId;
        private List<String> options = new ArrayList<>();
        private String variantSku;
        private List<ProductMedia> variantMedias = new ArrayList<>();
        private Double costPrice;
        private Double sellPrice;
        private String barcode;
        private Double compareAtPrice;
        private Double margin;
        private Dimension dimensions;
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
        private String address;
        private String pincode;
        private Integer stock;
    }


    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductMedia {
        private String mediaId;
        private String url; 
        private String title; 
        private String fileType; 
        private long fileSize; 
        private String createdAt;
    }

     @Data
     @NoArgsConstructor
     public static class ProductCategories {
        private String categoryId;
        private String slug;
     }

}



