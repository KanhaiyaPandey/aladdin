package com.store.aladdin.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "cart")
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {

    @Id
    private String cartId;
    private String userId;
    private List<CartItems> items;

    @Data
    @NoArgsConstructor
    public static class CartItems{
        private String productId;
        private String variantId;
        private String title;
        private String StockStatus;
        private List<String> attributes;
        private List<String> options;
        private Double price;
        private Integer quantity;
        private String image;
    }
}
