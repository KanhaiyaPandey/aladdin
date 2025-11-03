package com.store.aladdin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.store.aladdin.dtos.responseDTOs.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getFilteredProducts(String name, Double minPrice, Double maxPrice, String stockStatus) {
        return productQueries.filteredProducts(name, minPrice, maxPrice, stockStatus);
    }



    /// //////////////////////
    /// // update product
    /// /////////////////////

    public Product updateProduct(String productId, Product updatedProduct) {
        return productRepository.findById(productId).map(existingProduct -> {
            BeanUtils.copyProperties(updatedProduct, existingProduct, "id", "createdAt");
            existingProduct.setLastUpdatedAt(LocalDateTime.now());
            redisCacheService.delete(SINGLE_PRODUCT_CACHE_KEY + productId);
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }




    /// //////////////////////////////
    /// // update product variants
    /// /////////////////////////////

    public Product updateProductVariants(String productId, Product updatedProduct) {
        return productRepository.findById(productId).map(product -> {
            List<Product.Variant> variants = updatedProduct.getVariants(); // Fully qualified name
            if (variants != null) {
                variants.forEach(variant -> variant.setParentProductId(productId));
                product.setVariants(variants);
            }
            return productRepository.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
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

    public ProductResponse getProductById(String productId, Boolean isAdmin) throws Exception {
        try {
            String cacheKey = SINGLE_PRODUCT_CACHE_KEY + productId;
            Product cachedProduct = redisCacheService.get(cacheKey, Product.class);
            if (cachedProduct != null) {
                log.info("✅ Fetched full product from Redis cache: {}", productId);
                return new ProductResponse(cachedProduct, isAdmin);
            }
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found with ID: " + productId));
            redisCacheService.set(cacheKey, product, 500L);
            log.info("💾 Cached full product in Redis: {}", productId);
            return new ProductResponse(product, isAdmin);
        } catch (Exception e) {
            log.error("❌ Error while fetching product {}: {}", productId, e.getMessage());
            throw e;
        }
    }




}
