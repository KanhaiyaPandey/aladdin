package com.store.aladdin.services;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.store.aladdin.dtos.CategoryResponse;
import com.store.aladdin.dtos.PagedResponse;
import com.store.aladdin.dtos.productDTOs.ProductFilterRequest;
import com.store.aladdin.dtos.productDTOs.RelatedProductsDTO;
import com.store.aladdin.dtos.responseDTOs.CrossSellProductResponse;
import com.store.aladdin.dtos.responseDTOs.ProductResponse;
import com.store.aladdin.utils.helper.StockHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import static com.store.aladdin.keys.CacheKeys.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueries productQueries;
    private final RedisCacheService redisCacheService;
    private final CategoryService categoryService;

    private final ExecutorService executor = Executors.newFixedThreadPool(
            Math.max(4, Runtime.getRuntime().availableProcessors())
    );



    /// //////////////////////
    /// // create product
    /// /////////////////////

    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setLastUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        try {
            redisCacheService.set(SINGLE_PRODUCT_CACHE_KEY + savedProduct.getProductId(), savedProduct, 500L);
            log.info("💾 Cached product with key: {}", SINGLE_PRODUCT_CACHE_KEY + savedProduct.getProductId());
        } catch (Exception e) {
            log.error("❌ Failed to cache product in Redis: {}", e.getMessage());
        }
        StockHelper.enrich(savedProduct);
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * @param activeOnly true for the public catalogue (DRAFT products are never shown to shoppers),
     *                    false for the admin product list (sees everything, any status).
     */
    public PagedResponse<Product> getFilteredProducts(ProductFilterRequest filter, boolean activeOnly) {
        PagedResponse<Product> page = productQueries.filteredProducts(filter, activeOnly);
        StockHelper.enrichVariantsOnly(page.getItems());
        return page;
    }



    /// //////////////////////
    /// // update product
    /// /////////////////////

    public Product updateProduct(String productId, Product updatedProduct) {
        Product saved = productRepository.findById(productId).map(existingProduct -> {
            BeanUtils.copyProperties(updatedProduct, existingProduct, "id", "createdAt");
            existingProduct.setLastUpdatedAt(LocalDateTime.now());
            redisCacheService.delete(SINGLE_PRODUCT_CACHE_KEY + productId);
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        StockHelper.enrich(saved);
        return saved;
    }




    /// //////////////////////////////
    /// // update product variants
    /// /////////////////////////////

    public Product updateProductVariants(String productId, Product updatedProduct) {
        Product saved = productRepository.findById(productId).map(product -> {
            List<Product.Variant> variants = updatedProduct.getVariants(); // Fully qualified name
            if (variants != null) {
                variants.forEach(variant -> variant.setParentProductId(productId));
                product.setVariants(variants);
            }
            return productRepository.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        StockHelper.enrich(saved);
        return saved;
    }





/// //////////////////////
// delete product
/// /////////////////////

    public void deleteProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }




    /// //////////////////////
    /// // get product by id
    /// /////////////////////

    public ProductResponse getProductById(String productId) throws Exception {
        try {
            String cacheKey = SINGLE_PRODUCT_CACHE_KEY + productId;
            Product product = redisCacheService.get(cacheKey, Product.class);
            if (product == null) {
                product = productRepository.findById(productId)
                        .orElseThrow(() -> new Exception("Product not found with ID: " + productId));
                redisCacheService.set(cacheKey, product, 500L);
                log.info("💾 Cached full product in Redis: {}", productId);
            } else {
                log.info("✅ Fetched full product from Redis cache: {}", productId);
            }
            // Recompute stock even for a cache hit - warehouse stock can change
            // well within the cache TTL and the frontend must never show a
            // stale "in stock" for something that just sold out.
            StockHelper.enrich(product);
            ProductResponse response = new ProductResponse(product);
            if (product.getProductCategories() != null && !product.getProductCategories().isEmpty()) {
                List<CategoryResponse> categoryResponses = product.getProductCategories().stream()
                        .map(cat -> categoryService.getCategoryById(cat.getCategoryId())) // call the method you provided
                        .filter(Objects::nonNull) // avoid nulls if category not found
                        .toList();
                response.setProductCategories(categoryResponses);
            }
            return response;
        } catch (Exception e) {
            log.error("❌ Error while fetching product {}: {}", productId, e.getMessage());
            throw e;
        }
    }


    public RelatedProductsDTO getRelatedProducts(String productId) throws Exception {
        Product mainProduct = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found with ID: " + productId));
        RelatedProductsDTO dto = new RelatedProductsDTO();
        dto.setCrossSellProducts(
                fetchProductResponses(mainProduct.getCrossSellProducts())
        );
        dto.setUpSellProducts(
                fetchProductResponses(mainProduct.getUpSellProducts())
        );
        return dto;
    }

    private List<CrossSellProductResponse> fetchProductResponses(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) return new ArrayList<>();
        List<CrossSellProductResponse> result = new ArrayList<>();
        for (String id : productIds) {
            try {
                ProductResponse product = getProductById(id);
                result.add(new CrossSellProductResponse(product));
            } catch (Exception ignored) {
                // Skip missing IDs
            }
        }
        return result;
    }


}
