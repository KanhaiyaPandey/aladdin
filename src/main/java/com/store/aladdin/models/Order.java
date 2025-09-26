package com.store.aladdin.models;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import static com.store.aladdin.utils.helper.Enums.*;



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
    private String customerId;
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
    private String gstNumber;


    // constructors
  



    @Data
    @NoArgsConstructor
    public static class Address {


        private String firstName; 
        private String lastName; 
        private String houseNumber;
        private String area; 
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

    private String shipRocketOrderId;
    private String shipmentId;
    private String status;
    private String awbCode;
    private String courierName;
    private String packagingBoxError;
    
}

@Data
@NoArgsConstructor
public static class PaymentInfo{
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    private String paymentMode;
}



    
}
