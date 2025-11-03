package com.store.aladdin.dtos;

import com.store.aladdin.models.Product;
import com.store.aladdin.utils.helper.Enums;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private String productId;
    private String title;
    private String description;
    private Double sellPrice;
    private Double compareAtPrice;
    private String sku;
    private boolean allowBackorder;
    private Enums.StockStatus stockStatus;
    private String barcode;
    private List<String> attributes = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Product.Variant> variants = new ArrayList<>();
    private List<Product.ProductMedia> productMedias = new ArrayList<>();
    private List<Product.Dimension> dimensions = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public ProductDto(String productId, String title, String description, Double sellPrice, Double compareAtPrice,
                      String sku, boolean allowBackorder, Enums.StockStatus stockStatus, String barcode,
                      List<String> attributes, List<String> tags, List<Product.Variant> variants,
                      List<Product.ProductMedia> productMedias, List<Product.Dimension> dimensions,
                      LocalDateTime createdAt, LocalDateTime lastUpdatedAt) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.sellPrice = sellPrice;
        this.compareAtPrice = compareAtPrice;
        this.sku = sku;
        this.allowBackorder = allowBackorder;
        this.stockStatus = stockStatus;
        this.barcode = barcode;
        this.attributes = attributes != null ? attributes : new ArrayList<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.variants = variants != null ? variants : new ArrayList<>();
        this.productMedias = productMedias != null ? productMedias : new ArrayList<>();
        this.dimensions = dimensions != null ? dimensions : new ArrayList<>();
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
