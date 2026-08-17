package com.store.aladdin.dtos.productDTOs;

import lombok.Data;

/**
 * Every product search/filter/sort/paging option the public catalogue and
 * the admin product list accept, bound straight from query params via
 * {@code @ModelAttribute}. Keeping this in one place is what let both
 * controllers drop their long {@code @RequestParam} lists.
 */
@Data
public class ProductFilterRequest {

    /** Matches against title, tags and SKU (case-insensitive). */
    private String name;

    private Double minPrice;
    private Double maxPrice;

    /** One of Enums.StockStatus: IN_STOCK | LIMITED_STOCK | OUT_OF_STOCK. */
    private String stockStatus;

    /** Category slug or id. */
    private String category;

    /** When true (default) and `category` is a parent, products in its subcategories are included too. */
    private boolean includeSubcategories = true;

    private int page = 0;
    private int size = 20;

    /** newest (default) | price_asc | price_desc | title */
    private String sortBy = "newest";

    public int getPage() {
        return Math.max(page, 0);
    }

    public int getSize() {
        if (size <= 0) {
            return 20;
        }
        return Math.min(size, 100);
    }
}
