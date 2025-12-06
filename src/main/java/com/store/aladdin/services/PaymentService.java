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
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;


    public JSONObject createOrder(double amount, String userId) throws RazorpayException {
        int finalAmount = (int) (amount * 100);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", finalAmount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcpt_" + UUID.randomUUID().toString());

        Order razorOrder = razorpayClient.orders.create(orderRequest);

        // Extract order ID from Razorpay response
        String razorpayOrderId = razorOrder.get("id");

        // Save local payment record
        Payment payment = Payment.builder()
                .razorpayOrderId(razorpayOrderId)
                .status("PENDING")
                .currency("INR")
                .amount(amount)
                .userId(userId)
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
            String generatedSignature = Base64.getEncoder().encodeToString(hash);
            
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying payment signature: " + e.getMessage(), e);
        }
    }

    public Payment markSuccess(String orderId, String paymentId, String signature) {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
        
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment record not found for order: " + orderId);
        }

        Payment payment = paymentOpt.get();
        payment.setRazorpayPaymentId(paymentId);
        payment.setRazorpaySignature(signature);
        payment.setStatus("SUCCESS");
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
    public boolean verifyWebhookSignature(String webhookBody, String webhookSignature) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpaySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            sha256.init(secretKey);

            byte[] hash = sha256.doFinal(webhookBody.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = Base64.getEncoder().encodeToString(hash);

            return generatedSignature.equals(webhookSignature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying webhook signature: " + e.getMessage(), e);
        }
    }

}
