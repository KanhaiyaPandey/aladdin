package com.store.aladdin.dtos.responseDTOs;

import com.store.aladdin.models.Product;
import com.store.aladdin.utils.helper.Enums;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CrossSellProductResponse {
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
