package com.store.aladdin.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import static com.store.aladdin.utils.helper.Enums.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "orders")
public class Order {

    @Id
    private String orderId;

    private String customerId;

    private String orderNumber;

    private Address shippingAddress;

    private List<OrderItem> items;

    private OrderStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private PaymentStatus paymentStatus;

    private PaymentMode paymentMode;

    private Double shippingCharges;

    private Double extraCharges;

    private Double discountAmount;

    private Double grandTotal;

    private Double gatewayDiscount;

    private LocalDateTime deliveredDate;

    private List<Timeline> timeline;

    private ShippingDetails shippingDetails;

    private PaymentInfo paymentInfo;

    private String gstNumber;

    // ---------- Nested Classes ---------- //

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
        private String title;
        private List<String> attributes;
        private List<String> options;
        private String media;
        private int quantity;
        private double priceSnapshot;
    }

    @Data
    @NoArgsConstructor
    public static class Timeline {
        private OrderStatus status;
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
    public static class PaymentInfo {
        private String razorpayPaymentId;
        private String razorpayOrderId;
        private String razorpaySignature;
    }
}
