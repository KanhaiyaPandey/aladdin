package com.store.aladdin.queries;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderQueries {


    public static Query buildOrderQuery(
            String userName,
            String minPrice,
            String maxPrice,
            String paymentStatus,
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime deliveryStartDate,
            LocalDateTime deliveryEndDate,
            String pincode,
            String orderId,
            String userId
    ) {
        Criteria criteria = new Criteria();

        if (userName != null && !userName.isEmpty()) {
            criteria.and("shippingAddress.firstName").is(userName);
        }

        if (minPrice != null && maxPrice != null) {
            criteria.and("grandTotal").gte(minPrice.toString()).lte(maxPrice.toString());
        } else if (minPrice != null) {
            criteria.and("grandTotal").gte(minPrice);
        } else if (maxPrice != null) {
            criteria.and("grandTotal").lte(maxPrice);
        }

        if (startDate != null && endDate != null) {
            criteria.and("createdAt").gte(startDate).lte(endDate);
        } else if (startDate != null) {
            criteria.and("createdAt").gte(startDate);
        } else if (endDate != null) {
            criteria.and("createdAt").lte(endDate);
        }

        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            criteria.and("paymentStatus").is(paymentStatus);
        }

        if (status != null && !status.isEmpty()) {
            criteria.and("status").is(status);
        }

        if (deliveryStartDate != null && deliveryEndDate != null) {
            criteria.and("deliveredDate").gte(deliveryStartDate).lte(deliveryEndDate);
        } else if (deliveryStartDate != null) {
            criteria.and("deliveredDate").gte(deliveryStartDate);
        } else if (deliveryEndDate != null) {
            criteria.and("deliveredDate").lte(deliveryEndDate);
        }

        if (pincode != null && !pincode.isEmpty()) {
            criteria.and("shippingAddress.pincode").is(pincode);
        }

        if (orderId != null && !orderId.isEmpty()) {
            criteria.and("orderId").is(orderId);
        }

        if (userId != null && !userId.isEmpty()) {
            criteria.and("userId").is(userId);
        }

        return new Query(criteria);
    }
}
