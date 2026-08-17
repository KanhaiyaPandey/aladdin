package com.store.aladdin.services.admin_services;

import com.store.aladdin.dtos.PagedResponse;
import com.store.aladdin.dtos.productDTOs.ProductFilterRequest;
import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.services.RedisCacheService;
import com.store.aladdin.utils.helper.StockHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.store.aladdin.keys.CacheKeys.SINGLE_PRODUCT_CACHE_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductQueries productQueries;
    private final RedisCacheService redisCacheService;

    public Product getProductById(String productId) throws Exception {
        try {
            String cacheKey = SINGLE_PRODUCT_CACHE_KEY + productId;
            Product product = redisCacheService.get(cacheKey, Product.class);
            if (product == null) {
                product = productRepository.findById(productId)
                        .orElseThrow(() -> new Exception("Product not found with ID: " + productId));
            }
            StockHelper.enrich(product);
            return product;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Admin product list/search - same filters as the public catalogue, but
     * never restricted to ACTIVE-only, so drafts and out-of-stock products
     * stay visible for management.
     */
    public PagedResponse<Product> getFilteredProducts(ProductFilterRequest filter) {
        PagedResponse<Product> page = productQueries.filteredProducts(filter, false);
        StockHelper.enrichVariantsOnly(page.getItems());
        return page;
    }
}
