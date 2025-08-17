package com.store.aladdin.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderFilterDto {
    private String userName;
    private String minPrice;
    private String maxPrice;
    private String paymentStatus;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime deliveryStartDate;
    private LocalDateTime deliveryEndDate;
    private String pincode;
    private String orderId;
    private String userId;
}
