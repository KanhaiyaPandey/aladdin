package com.store.aladdin.dtos.responseDTOs;

import com.store.aladdin.dtos.CategoryResponse;
import com.store.aladdin.models.Product;
import com.store.aladdin.utils.helper.Enums;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductResponse {

    private String productId;
    private String title;
    private String description;
    private Double sellPrice;
    private Double costPrice;
    private Double compareAtPrice;
    private Double mrp;
    private String sku;
    private boolean allowBackorder;
    private Enums.StockStatus stockStatus;
    private String barcode;
    private String sizeGuide;
    private List<String> attributes = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Product.Variant> variants = new ArrayList<>();
    private List<Product.Warehouse>warehouseData = new ArrayList<>();
    private List<Product.ProductMedia> productMedias = new ArrayList<>();
    private List<Product.Dimension> dimensions = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<CategoryResponse>productCategories;
    private List<String> upSellProducts;
    private List<String> crossSellProducts;

    public ProductResponse(Product product) {
        this.productId = product.getProductId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.sellPrice = product.getSellPrice();
        this.mrp = product.getCompareAtPrice();
        this.sku = product.getSku();
        this.allowBackorder = product.isAllowBackorder();
        this.stockStatus = product.getStockStatus();
        this.barcode = product.getBarcode();
        this.attributes = product.getAttributes() != null ? product.getAttributes() : new ArrayList<>();
        this.tags = product.getTags() != null ? product.getTags() : new ArrayList<>();
        this.variants = product.getVariants() != null ? product.getVariants() : new ArrayList<>();
        this.productMedias = product.getProductMedias() != null ? product.getProductMedias() : new ArrayList<>();
        this.dimensions = product.getDimensions() != null ? product.getDimensions() : new ArrayList<>();
        this.createdAt = product.getCreatedAt();
        this.lastUpdatedAt = product.getLastUpdatedAt();
        this.upSellProducts = product.getUpSellProducts();
        this.crossSellProducts = product.getCrossSellProducts();
        this.sizeGuide = product.getSizeGuide();
    }
}
