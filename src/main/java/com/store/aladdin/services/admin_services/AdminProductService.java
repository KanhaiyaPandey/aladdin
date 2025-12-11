package com.store.aladdin.services.admin_services;

import com.store.aladdin.models.Product;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.services.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.store.aladdin.keys.CacheKeys.SINGLE_PRODUCT_CACHE_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {

    private final ProductRepository productRepository;
    private final RedisCacheService redisCacheService;

    public Product getProductById(String productId) throws Exception {
        log.info("hello");
        try{
            String cacheKey = SINGLE_PRODUCT_CACHE_KEY + productId;
            Product product = redisCacheService.get(cacheKey, Product.class);
            if(product != null){
                return product;
            }
            return productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found with ID: " + productId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
