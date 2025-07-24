package com.store.aladdin.utils.helper;

import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class Enums {

    public enum Status {
        Active,
        Draft
    }

    public enum StockStatus {
        OUT_OF_STOCK,
        IN_STOCK,
        LIMITED_STOCK
    }

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

    
}
