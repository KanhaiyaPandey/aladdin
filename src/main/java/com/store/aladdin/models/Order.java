package com.store.aladdin.models;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.store.aladdin.utils.helper.Enums.OrderStatus;
import com.store.aladdin.utils.helper.Enums.PaymentMode;
import com.store.aladdin.utils.helper.Enums.PaymentStatus;


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
    private String gatewayDiscount;
    private ShippingDetails shippingDetails;
    private PaymentInfo paymentInfo;
  



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

@Data
@NoArgsConstructor
public static class ShippingDetails {

    private String order_id;
    private String shipment_id;
    private String status;
    private String awb_code;
    private String courier_name;
    private String packaging_box_error;
    
}

@Data
@NoArgsConstructor
public static class PaymentInfo{
    private String razorpay_payment_id;
    private String razorpay_order_id;
    private String razorpay_signature;
    private String paymentMode;
}


    
}
