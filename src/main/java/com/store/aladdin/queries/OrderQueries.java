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

        addStringCriteria(criteria, "shippingAddress.firstName", userName);
        addRangeCriteria(criteria, "grandTotal", minPrice, maxPrice);
        addRangeCriteria(criteria, "createdAt", startDate, endDate);
        addStringCriteria(criteria, "paymentStatus", paymentStatus);
        addStringCriteria(criteria, "status", status);
        addRangeCriteria(criteria, "deliveredDate", deliveryStartDate, deliveryEndDate);
        addStringCriteria(criteria, "shippingAddress.pincode", pincode);
        addStringCriteria(criteria, "orderId", orderId);
        addStringCriteria(criteria, "userId", userId);

        return new Query(criteria);
    }

    private static void addStringCriteria(Criteria criteria, String field, String value) {
        if (value != null && !value.isEmpty()) {
            criteria.and(field).is(value);
        }
    }

    private static <T extends Comparable<T>> void addRangeCriteria(Criteria criteria, String field, T min, T max) {
        if (min != null && max != null) {
            criteria.and(field).gte(min).lte(max);
        } else if (min != null) {
            criteria.and(field).gte(min);
        } else if (max != null) {
            criteria.and(field).lte(max);
        }
    }
}
