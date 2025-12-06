package com.store.aladdin.services;

import com.store.aladdin.models.Payment;
import com.store.aladdin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Production-level security service for payment fraud prevention
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSecurityService {

    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Rate limiting constants
    private static final int MAX_PAYMENT_ATTEMPTS_PER_HOUR = 10;
    private static final int MAX_PAYMENT_ATTEMPTS_PER_DAY = 50;
    private static final int MAX_VERIFICATION_ATTEMPTS = 3;
    private static final double AMOUNT_TOLERANCE_PERCENT = 0.01; // 1% tolerance

    /**
     * Check if user has exceeded rate limits for payment creation
     */
    public boolean isRateLimited(String userId) {
        String hourlyKey = "payment:rate:user:" + userId + ":hour";
        String dailyKey = "payment:rate:user:" + userId + ":day";

        Long hourlyCount = redisTemplate.opsForValue().increment(hourlyKey);
        Long dailyCount = redisTemplate.opsForValue().increment(dailyKey);

        // Set TTL if first request
        if (hourlyCount != null && hourlyCount == 1) {
            redisTemplate.expire(hourlyKey, 1, TimeUnit.HOURS);
        }
        if (dailyCount != null && dailyCount == 1) {
            redisTemplate.expire(dailyKey, 24, TimeUnit.HOURS);
        }

        if (hourlyCount != null && hourlyCount > MAX_PAYMENT_ATTEMPTS_PER_HOUR) {
            log.warn("⚠️ Rate limit exceeded for user {}: {} attempts in last hour", userId, hourlyCount);
            return true;
        }

        if (dailyCount != null && dailyCount > MAX_PAYMENT_ATTEMPTS_PER_DAY) {
            log.warn("⚠️ Daily rate limit exceeded for user {}: {} attempts today", userId, dailyCount);
            return true;
        }

        return false;
    }

    /**
     * Validate amount to prevent tampering
     * Compares the payment amount with the stored payment record
     */
    public boolean validateAmount(String razorpayOrderId, Double incomingAmount) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentOpt.isEmpty()) {
            log.error("❌ Payment record not found for order: {}", razorpayOrderId);
            return false;
        }

        Payment payment = paymentOpt.get();
        Double storedAmount = payment.getAmount();

        if (storedAmount == null || incomingAmount == null) {
            log.error("❌ Amount validation failed: null values detected");
            return false;
        }

        // Calculate tolerance
        double tolerance = storedAmount * AMOUNT_TOLERANCE_PERCENT;
        double difference = Math.abs(storedAmount - incomingAmount);

        if (difference > tolerance) {
            log.error("❌ Amount tampering detected! Stored: {}, Incoming: {}, Difference: {}", 
                    storedAmount, incomingAmount, difference);
            return false;
        }

        return true;
    }

    /**
     * Check if payment verification attempts exceeded
     */
    public boolean hasExceededVerificationAttempts(String razorpayOrderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentOpt.isEmpty()) {
            return false;
        }

        Payment payment = paymentOpt.get();
        Integer attempts = payment.getVerificationAttempts();

        if (attempts == null) {
            return false;
        }

        if (attempts >= MAX_VERIFICATION_ATTEMPTS) {
            log.warn("⚠️ Verification attempts exceeded for order {}: {} attempts", 
                    razorpayOrderId, attempts);
            return true;
        }

        return false;
    }

    /**
     * Increment verification attempts counter
     */
    public void incrementVerificationAttempts(String razorpayOrderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            Integer attempts = payment.getVerificationAttempts();
            payment.setVerificationAttempts(attempts == null ? 1 : attempts + 1);
            payment.setLastVerificationAttempt(LocalDateTime.now());
            paymentRepository.save(payment);
        }
    }

    /**
     * Check for duplicate payment processing (idempotency)
     */
    public boolean isDuplicatePayment(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            return false;
        }

        String redisKey = "payment:idempotency:" + idempotencyKey;
        Boolean exists = redisTemplate.hasKey(redisKey);

        if (Boolean.TRUE.equals(exists)) {
            log.warn("⚠️ Duplicate payment detected with idempotency key: {}", idempotencyKey);
            return true;
        }

        // Store idempotency key for 24 hours
        redisTemplate.opsForValue().set(redisKey, "processed", 24, TimeUnit.HOURS);
        return false;
    }

    /**
     * Check if payment is already processed
     */
    public boolean isPaymentAlreadyProcessed(String razorpayOrderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentOpt.isEmpty()) {
            return false;
        }

        Payment payment = paymentOpt.get();
        String status = payment.getStatus();

        // Check if payment is already successful
        if ("SUCCESS".equals(status) && payment.getOrderId() != null) {
            log.warn("⚠️ Payment {} already processed successfully with order {}", 
                    razorpayOrderId, payment.getOrderId());
            return true;
        }

        return false;
    }

    /**
     * Validate payment belongs to user
     */
    public boolean validatePaymentOwnership(String razorpayOrderId, String userId) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentOpt.isEmpty()) {
            return false;
        }

        Payment payment = paymentOpt.get();
        return userId.equals(payment.getUserId());
    }

    /**
     * Detect suspicious activity patterns
     */
    public boolean detectSuspiciousActivity(String userId, String clientIp) {
        // Check for multiple payments from different IPs in short time
        String ipKey = "payment:ip:user:" + userId;
        String lastIp = (String) redisTemplate.opsForValue().get(ipKey);

        if (lastIp != null && !lastIp.equals(clientIp)) {
            log.warn("⚠️ Suspicious activity: User {} payment from different IP. Previous: {}, Current: {}", 
                    userId, lastIp, clientIp);
            // In production, you might want to flag this for manual review
        }

        redisTemplate.opsForValue().set(ipKey, clientIp, 1, TimeUnit.HOURS);
        return false; // For now, just log. Can be enhanced for stricter checks.
    }

}

