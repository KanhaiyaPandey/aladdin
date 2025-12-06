package com.store.aladdin.dtos.paymentDTOs;

import com.store.aladdin.dtos.orderDTOs.OrderRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationDTO {

    private String razorpay_order_id;
    private String razorpay_payment_id;
    private String razorpay_signature;
    private OrderRequestDTO orderRequest;
    
    // Idempotency key to prevent duplicate processing
    private String idempotencyKey;
    
    // Amount validation (should match stored payment)
    private Double amount;

}
