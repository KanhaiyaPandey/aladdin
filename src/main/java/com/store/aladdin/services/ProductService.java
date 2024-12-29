package com.store.aladdin.services;

import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.store.aladdin.models.Product;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.ResourceNotFoundException;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    public void createProduct(Product product){
        product.setDate(LocalDateTime.now());
        productRepository.save(product);
   
    }

    public List<Product> getAllProducts() {
      return productRepository.findAll();
    }

    public Product updateProduct(ObjectId productId, Product updatedProduct) {
      return productRepository.findById(productId).map(product -> {
        System.out.println("to upadate product"+product);
          product.setTitle(updatedProduct.getTitle());
          product.setDescription(updatedProduct.getDescription());
          product.setDate(LocalDateTime.now());
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
