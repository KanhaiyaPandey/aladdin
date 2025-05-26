package com.store.aladdin.models;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String orderId;
    private String userId;
    private User customerDetails;
    private Address shippingAddress;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private PaymentStatus paymentStatus;
    private PaymentMode paymentMode;
    private String shippingCharges;
    private String extraCharges;
    private String discountAmount;
    private String grandTotal;
    private LocalDateTime deliveredDate;
    private List<Timeline> timeline;
    private double gatewayDiscount;
  
    

    // Enum for order lifecycle
    public enum OrderStatus {
        PENDING, PROCESSING,CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    public enum PaymentStatus {
        PAID, PENDING
    }


    public enum PaymentMode {
        RAZORPAY, CASH_ON_DELIVERY
    }


    @Data
    @NoArgsConstructor
    public static class Address {


        private String firstName; 
        private String lastName; 
        private String address; 
        private String city; 
        private String state;
        private String pincode;
        private String email;
        private String phoneNumber;
    }

    @Data
    @NoArgsConstructor
    public static class OrderItem {
    private String productId;
    private String variantId;
    private List<String> attributes;
    private List<String> options;
    private String media;
    private int quantity;
    private double priceSnapshot; 
}

@Data
@NoArgsConstructor
public static class Timeline {

    private String status;
    private LocalDateTime time;
    
}
    
}
