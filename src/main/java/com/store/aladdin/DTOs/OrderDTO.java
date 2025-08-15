package com.store.aladdin.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {

    private String orderId;
    private String userId;
    private UserDTO customerDetails;
    private AddressDTO shippingAddress;
    private List<OrderItemDTO> items;
    private String status;
    private LocalDateTime createdAt;
    private String paymentStatus;
    private String paymentMode;
    private String shippingCharges;
    private String extraCharges;
    private String discountAmount;
    private String grandTotal;
    private LocalDateTime deliveredDate;
    private List<TimelineDTO> timeline;
    private double gatewayDiscount;

    @Data
    @NoArgsConstructor
    public static class AddressDTO {
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
    public static class OrderItemDTO {
        private String productId;
        private String variantId;
        private List<String> attributes;
        private List<String> options;
        private int quantity;
        private String media;
        private double priceSnapshot;
    }

    @Data
    @NoArgsConstructor
    public static class TimelineDTO {
        private String status;
        private LocalDateTime time;
    }

    // Optional: include minimal UserDTO if needed
    @Data
    @NoArgsConstructor
    public static class UserDTO {
        private String id;
        private String name;
        private String email;
    }
}

