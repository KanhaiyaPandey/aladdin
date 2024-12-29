package com.store.aladdin.utils;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class CartResponseItem {
    private String productId;
    private String productName;
    private String description;
    private double productPrice;
    private int quantity;

    public CartResponseItem(ObjectId productId, String productName, String description, double productPrice, Double double1, int quantity) {
        this.productId = productId.toHexString();
        this.productName = productName;
        this.description = description;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }
}
