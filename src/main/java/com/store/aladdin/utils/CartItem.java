package com.store.aladdin.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cart_items")
public class CartItem {

    @Id
    private ObjectId id; 
    private ObjectId productId; 
    private int quantity; 

        // Custom constructor
        public CartItem(ObjectId productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
}
