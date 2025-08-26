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
}
