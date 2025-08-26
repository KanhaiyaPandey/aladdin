package com.store.aladdin.services;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Product;
import com.store.aladdin.queries.ProductQueries;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueries productQueries;

    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setLastUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getFilteredProducts(String name, Double minPrice, Double maxPrice, String stockStatus) {
        return productQueries.filteredProducts(name, minPrice, maxPrice, stockStatus);
    }


    public Product updateProduct(ObjectId productId, Product updatedProduct) {
        return productRepository.findById(productId).map(existingProduct -> {
            BeanUtils.copyProperties(updatedProduct, existingProduct, "id", "createdAt");
            existingProduct.setLastUpdatedAt(LocalDateTime.now());
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }


    public Product updateProductVariants(ObjectId productId, Product updatedProduct) {
        return productRepository.findById(productId).map(product -> {
            List<Product.Variant> variants = updatedProduct.getVariants(); // Fully qualified name
            if (variants != null) {
                variants.forEach(variant -> variant.setParentProductId(productId.toHexString())); // Convert ObjectId to String
                product.setVariants(variants);
            }
            return productRepository.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }


    public void deleteProduct(ObjectId productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }


    public Product getProductById(ObjectId productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));
    }

}
