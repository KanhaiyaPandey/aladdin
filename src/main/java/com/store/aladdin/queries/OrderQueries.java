package com.store.aladdin.queries;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.store.aladdin.dtos.OrderFilterDto;


public class OrderQueries {

    private OrderQueries() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    public static Query buildOrderQuery(OrderFilterDto filter ) {
        Criteria criteria = new Criteria();


        addStringCriteria(criteria, "shippingAddress.firstName", filter.getUserName());
        addRangeCriteria(criteria, "grandTotal", filter.getMinPrice(), filter.getMaxPrice());
        addRangeCriteria(criteria, "createdAt", filter.getStartDate(), filter.getEndDate());
        addStringCriteria(criteria, "paymentStatus", filter.getPaymentStatus());
        addStringCriteria(criteria, "status", filter.getStatus());
        addRangeCriteria(criteria, "deliveredDate", filter.getDeliveryStartDate(), filter.getDeliveryEndDate());
        addStringCriteria(criteria, "shippingAddress.pincode", filter.getPincode());
        addStringCriteria(criteria, "orderId", filter.getOrderId());
        addStringCriteria(criteria, "userId", filter.getUserId());

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
