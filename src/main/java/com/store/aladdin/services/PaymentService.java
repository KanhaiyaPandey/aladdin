package com.store.aladdin.services;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.store.aladdin.models.Payment;
import com.store.aladdin.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;


    public JSONObject createOrder(double amount, String userId, String idempotencyKey, String clientIp, String userAgent) throws RazorpayException {
        // Validate amount range (production safety)
        if (amount <= 0 || amount > 10000000) { // Max 1 crore INR
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }

        int finalAmount = (int) (amount * 100);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", finalAmount);
        orderRequest.put("currency", "INR");
        String shortReceipt = "rcpt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        orderRequest.put("receipt", shortReceipt);

        Order razorOrder = razorpayClient.orders.create(orderRequest);

        // Extract order ID from Razorpay response
        String razorpayOrderId = razorOrder.get("id");

        // Save local payment record with security fields
        Payment payment = Payment.builder()
                .razorpayOrderId(razorpayOrderId)
                .status("PENDING")
                .currency("INR")
                .amount(amount)
                .userId(userId)
                .idempotencyKey(idempotencyKey)
                .verificationAttempts(0)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // Return Razorpay order response
        return razorOrder.toJson();
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpaySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            sha256.init(secretKey);

            byte[] hash = sha256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert byte[] to HEX string (Razorpay uses HEX)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            String generatedSignature = hexString.toString();

            return generatedSignature.equals(signature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment signature: " + e.getMessage(), e);
        }
    }

    public Payment markSuccess(String razorpayOrderId, String paymentId, String signature, String actualOrderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment record not found for order: " + razorpayOrderId);
        }

        Payment payment = paymentOpt.get();
        
        // Prevent duplicate processing
        if ("SUCCESS".equals(payment.getStatus()) && payment.getOrderId() != null) {
            throw new IllegalStateException("Payment already processed successfully");
        }

        payment.setRazorpayPaymentId(paymentId);
        payment.setRazorpaySignature(signature);
        payment.setStatus("SUCCESS");
        payment.setOrderId(actualOrderId);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Payment markFailed(String orderId, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
        
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment record not found for order: " + orderId);
        }

        Payment payment = paymentOpt.get();
        payment.setStatus("FAILED");
        payment.setFailureReason(reason);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentByOrderId(String razorpayOrderId) {
        return paymentRepository.findByRazorpayOrderId(razorpayOrderId);
    }

    /**
     * Verify webhook signature from Razorpay
     * @param webhookBody The raw webhook body as string
     * @param webhookSignature The X-Razorpay-Signature header value
     * @return true if signature is valid
     */
    /**
     * Verify webhook signature from Razorpay
     * Razorpay uses HMAC SHA256 with the webhook secret (same as API secret for simplicity)
     * The signature is sent as a hex string in X-Razorpay-Signature header
     * 
     * @param webhookBody The raw webhook body as string
     * @param webhookSignature The X-Razorpay-Signature header value (hex string)
     * @return true if signature is valid
     */
    public boolean verifyWebhookSignature(String webhookBody, String webhookSignature) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpaySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            sha256.init(secretKey);

            byte[] hash = sha256.doFinal(webhookBody.getBytes(StandardCharsets.UTF_8));
            
            // Convert byte[] to HEX string (Razorpay webhook signature is in HEX format)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            String generatedSignature = hexString.toString();

            return generatedSignature.equals(webhookSignature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying webhook signature: " + e.getMessage(), e);
        }
    }

}
