package com.store.aladdin.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    private String id;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private Double amount;
    private String currency;

    private String status;
    private String failureReason;

    private String userId;

    // Idempotency and fraud prevention
    private String idempotencyKey;
    private String orderId; // Link to actual order after creation
    private Integer verificationAttempts;
    private LocalDateTime lastVerificationAttempt;
    private String clientIp;
    private String userAgent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
