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
    private Double mrp;
    private String sku;
    private boolean allowBackorder;
    private Enums.StockStatus stockStatus;
    private String barcode;
    private List<String> attributes = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Product.Variant> variants = new ArrayList<>();
    private List<Product.ProductMedia> productMedias = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;


    public CrossSellProductResponse(ProductResponse product) {
        this.productId = product.getProductId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.sellPrice = product.getSellPrice();
        this.mrp = product.getCompareAtPrice();
        this.sku = product.getSku();
        this.allowBackorder = product.isAllowBackorder();
        this.stockStatus = product.getStockStatus();
        this.barcode = product.getBarcode();
        this.tags = product.getTags();
        this.attributes = product.getAttributes();
        this.variants = product.getVariants();
        this.productMedias = product.getProductMedias();
        this.createdAt = product.getCreatedAt();
        this.lastUpdatedAt = product.getLastUpdatedAt();
    }
}


