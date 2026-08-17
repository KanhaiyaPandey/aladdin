package com.store.aladdin.utils.helper;

import java.util.List;

import com.store.aladdin.models.Product;
import com.store.aladdin.utils.helper.Enums.StockStatus;

/**
 * Single source of truth for turning raw warehouse stock rows into the
 * stock/availability info the frontend actually needs.
 *
 * Product.stockStatus used to be a plain field the admin set by hand and that
 * never changed again, so it silently drifted from the real warehouse
 * numbers. Every product/variant handed back to a controller should instead
 * go through {@link #enrich(Product)} (or {@link #enrichVariantsOnly(Product)}
 * when the product-level total was already computed by an aggregation
 * pipeline) so stock is always derived, never stale.
 */
public final class StockHelper {

    /** Stock at/under this line is reported as LIMITED_STOCK instead of IN_STOCK. */
    public static final int LOW_STOCK_THRESHOLD = 5;

    private StockHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static int sumStock(List<Product.Warehouse> warehouseData) {
        if (warehouseData == null || warehouseData.isEmpty()) {
            return 0;
        }
        return warehouseData.stream()
                .mapToInt(w -> w.getStock() != null ? w.getStock() : 0)
                .sum();
    }

    public static StockStatus statusFor(int totalStock) {
        if (totalStock <= 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (totalStock <= LOW_STOCK_THRESHOLD) {
            return StockStatus.LIMITED_STOCK;
        }
        return StockStatus.IN_STOCK;
    }

    /**
     * Fully (re)computes stock for a product and every one of its variants
     * from scratch. Use this whenever the product wasn't already run through
     * the stock-aware aggregation pipeline (single-product fetch, create,
     * update, related/cross-sell lookups, ...).
     */
    public static void enrich(Product product) {
        if (product == null) {
            return;
        }
        boolean hasVariants = product.getVariants() != null && !product.getVariants().isEmpty();
        int productTotal;
        boolean anyVariantInStock = false;

        if (hasVariants) {
            int sum = 0;
            for (Product.Variant variant : product.getVariants()) {
                int variantStock = sumStock(variant.getVariantWarehouseData());
                StockStatus variantStatus = statusFor(variantStock);
                variant.setTotalStock(variantStock);
                variant.setStockStatus(variantStatus);
                variant.setInStock(variantStatus != StockStatus.OUT_OF_STOCK);
                variant.setPurchasable(variant.isInStock() || product.isAllowBackorder());
                sum += variantStock;
                anyVariantInStock = anyVariantInStock || variant.isInStock();
            }
            productTotal = sum;
        } else {
            productTotal = sumStock(product.getWarehouseData());
            anyVariantInStock = productTotal > 0;
        }

        product.setTotalStock(productTotal);
        product.setStockStatus(statusFor(productTotal));
        product.setInStock(hasVariants ? anyVariantInStock : productTotal > 0);
        product.setPurchasable(product.isInStock() || product.isAllowBackorder());
    }

    public static void enrich(List<Product> products) {
        if (products == null) {
            return;
        }
        products.forEach(StockHelper::enrich);
    }

    /**
     * Enriches only the variant-level stock detail, trusting that
     * product-level totalStock/stockStatus were already computed upstream
     * (e.g. by the $addFields stage in {@code ProductQueries}). Avoids
     * redoing the same sum twice for list/search results.
     */
    public static void enrichVariantsOnly(Product product) {
        if (product == null) {
            return;
        }
        boolean hasVariants = product.getVariants() != null && !product.getVariants().isEmpty();
        boolean anyVariantInStock = false;

        if (hasVariants) {
            for (Product.Variant variant : product.getVariants()) {
                int variantStock = sumStock(variant.getVariantWarehouseData());
                StockStatus variantStatus = statusFor(variantStock);
                variant.setTotalStock(variantStock);
                variant.setStockStatus(variantStatus);
                variant.setInStock(variantStatus != StockStatus.OUT_OF_STOCK);
                variant.setPurchasable(variant.isInStock() || product.isAllowBackorder());
                anyVariantInStock = anyVariantInStock || variant.isInStock();
            }
        }

        Integer total = product.getTotalStock();
        product.setInStock(hasVariants ? anyVariantInStock : (total != null && total > 0));
        product.setPurchasable(product.isInStock() || product.isAllowBackorder());
    }

    public static void enrichVariantsOnly(List<Product> products) {
        if (products == null) {
            return;
        }
        products.forEach(StockHelper::enrichVariantsOnly);
    }
}
