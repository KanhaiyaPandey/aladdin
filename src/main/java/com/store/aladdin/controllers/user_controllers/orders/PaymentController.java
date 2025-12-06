package com.store.aladdin.controllers.user_controllers.orders;

import com.razorpay.RazorpayException;
import com.store.aladdin.dtos.orderDTOs.OrderRequestDTO;
import com.store.aladdin.dtos.paymentDTOs.PaymentVerificationDTO;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Order;
import com.store.aladdin.models.Payment;
import com.store.aladdin.repository.OrderRepository;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.OrderService;
import com.store.aladdin.services.PaymentService;
import com.store.aladdin.services.PaymentSecurityService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.store.aladdin.routes.UserRoutes.*;
import static com.store.aladdin.routes.AuthRoutes.*;


@Slf4j
@RestController
@RequestMapping(USER_BASE)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final PaymentSecurityService paymentSecurityService;

    /**
     * Step 1: Create Razorpay Order
     * This endpoint creates a payment order in Razorpay and returns the order details
     * to the frontend for initiating the payment.
     */
    @PostMapping(RAZORPAY_CREATE_ORDER)
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam double amount,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String token = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);

            // Get client IP and user agent for fraud detection
            String clientIp = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");

            // Generate idempotency key if not provided
            if (idempotencyKey == null || idempotencyKey.isEmpty()) {
                idempotencyKey = UUID.randomUUID().toString();
            }

            // ✅ FRAUD CHECK 1: Rate limiting
            if (paymentSecurityService.isRateLimited(userId)) {
                return ResponseUtil.buildResponse(
                    "Too many payment requests. Please try again later.",
                    false,
                    null,
                    HttpStatus.TOO_MANY_REQUESTS
                );
            }

            // ✅ FRAUD CHECK 2: Suspicious activity detection
            paymentSecurityService.detectSuspiciousActivity(userId, clientIp);

            // Validate amount (already done in service, but double-check here)
            if (amount <= 0 || amount > 10000000) {
                return ResponseUtil.buildResponse(
                    "Invalid amount. Amount must be between 0 and 10,000,000",
                    false,
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }

            // Create Razorpay order with security fields
            JSONObject razorpayOrder = paymentService.createOrder(amount, userId, idempotencyKey, clientIp, userAgent);

            // Convert JSONObject to Map for response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", razorpayOrder.getString("id"));
            response.put("amount", razorpayOrder.getInt("amount"));
            response.put("currency", razorpayOrder.getString("currency"));
            response.put("status", razorpayOrder.getString("status"));
            response.put("receipt", razorpayOrder.optString("receipt", ""));
            response.put("idempotencyKey", idempotencyKey);

            return ResponseUtil.buildResponse(
                "Razorpay order created successfully",
                true,
                response,
                HttpStatus.CREATED
            );
        } catch (RazorpayException e) {
            throw new CustomeRuntimeExceptionsHandler(
                "Failed to create Razorpay order: " + e.getMessage()
            );
        } catch (IllegalArgumentException e) {
            return ResponseUtil.buildResponse(
                e.getMessage(),
                false,
                null,
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler(
                "Error creating payment order: " + e.getMessage()
            );
        }
    }

    /**
     * Step 2: Verify payment signature and create order
     * This endpoint verifies the payment signature from Razorpay webhook/callback
     * and creates the actual order in the system.
     * 
     * Request body should contain:
     * {
     *   "razorpay_order_id": "...",
     *   "razorpay_payment_id": "...",
     *   "razorpay_signature": "...",
     *   "orderRequest": { ... OrderRequestDTO ... }
     * }
     */
    @PostMapping(VARIFY_PAYMENT)
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @Valid @RequestBody PaymentVerificationDTO verificationDTO,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String token = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);

            // Extract payment details
            String orderId = verificationDTO.getRazorpay_order_id();
            String paymentId = verificationDTO.getRazorpay_payment_id();
            String signature = verificationDTO.getRazorpay_signature();
            OrderRequestDTO orderRequestDTO = verificationDTO.getOrderRequest();
            String idempotencyKey = verificationDTO.getIdempotencyKey();
            Double incomingAmount = verificationDTO.getAmount();

            // ✅ IDEMPOTENCY CHECK: Prevent duplicate processing
            if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
                if (paymentSecurityService.isDuplicatePayment(idempotencyKey)) {
                    // Return existing order if already processed
                    Optional<Payment> existingPayment = paymentService.getPaymentByOrderId(orderId);
                    if (existingPayment.isPresent() && "SUCCESS".equals(existingPayment.get().getStatus())) {
                        String existingOrderId = existingPayment.get().getOrderId();
                        if (existingOrderId != null) {
                            return orderRepository.findById(existingOrderId)
                                .map(order -> ResponseUtil.buildResponse(
                                    "Payment already processed",
                                    true,
                                    order,
                                    HttpStatus.OK
                                ))
                                .orElse(ResponseUtil.buildResponse(
                                    "Duplicate payment detected",
                                    false,
                                    null,
                                    HttpStatus.CONFLICT
                                ));
                        }
                    }
                    return ResponseUtil.buildResponse(
                        "Duplicate payment request detected",
                        false,
                        null,
                        HttpStatus.CONFLICT
                    );
                }
            }

            // ✅ FRAUD CHECK 1: Check if payment already processed
            if (paymentSecurityService.isPaymentAlreadyProcessed(orderId)) {
                return ResponseUtil.buildResponse(
                    "Payment already processed successfully",
                    false,
                    null,
                    HttpStatus.CONFLICT
                );
            }

            // ✅ FRAUD CHECK 2: Check verification attempts
            if (paymentSecurityService.hasExceededVerificationAttempts(orderId)) {
                return ResponseUtil.buildResponse(
                    "Too many verification attempts. Please contact support.",
                    false,
                    null,
                    HttpStatus.TOO_MANY_REQUESTS
                );
            }

            // ✅ FRAUD CHECK 3: Validate payment ownership
            if (!paymentSecurityService.validatePaymentOwnership(orderId, userId)) {
                paymentSecurityService.incrementVerificationAttempts(orderId);
                return ResponseUtil.buildResponse(
                    "Unauthorized: Payment does not belong to this user",
                    false,
                    null,
                    HttpStatus.FORBIDDEN
                );
            }

            // ✅ SECURITY CHECK: Amount validation (prevent tampering)
            if (incomingAmount != null) {
                if (!paymentSecurityService.validateAmount(orderId, incomingAmount)) {
                    paymentSecurityService.incrementVerificationAttempts(orderId);
                    paymentService.markFailed(orderId, "Amount tampering detected");
                    return ResponseUtil.buildResponse(
                        "Amount validation failed. Payment amount does not match.",
                        false,
                        null,
                        HttpStatus.BAD_REQUEST
                    );
                }
            }

            // Validate that order request is provided
            if (orderRequestDTO == null) {
                return ResponseUtil.buildResponse(
                    "Order details are required to complete the order",
                    false,
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }

            // ✅ SECURITY CHECK: Verify payment signature
            boolean isValid = paymentService.verifySignature(orderId, paymentId, signature);

            if (!isValid) {
                paymentSecurityService.incrementVerificationAttempts(orderId);
                paymentService.markFailed(orderId, "Signature verification failed");
                return ResponseUtil.buildResponse(
                    "Payment verification failed: Invalid signature",
                    false,
                    null,
                    HttpStatus.OK
                );
            }

            // Create the actual order in the system FIRST (before marking payment success)
            Order finalOrder = orderService.createOrder(orderRequestDTO, userId);

            // Update payment status in DB with order ID
            paymentService.markSuccess(orderId, paymentId, signature, finalOrder.getOrderId());

            // Update order with payment information
            if (finalOrder.getPaymentInfo() == null) {
                finalOrder.setPaymentInfo(new Order.PaymentInfo());
            }
            finalOrder.getPaymentInfo().setRazorpayOrderId(orderId);
            finalOrder.getPaymentInfo().setRazorpayPaymentId(paymentId);
            finalOrder.getPaymentInfo().setRazorpaySignature(signature);

            // Update payment status in order
            finalOrder.setPaymentStatus(com.store.aladdin.utils.helper.Enums.PaymentStatus.PAID);

            // Save the updated order with payment information
            finalOrder = orderRepository.save(finalOrder);

            return ResponseUtil.buildResponse(
                "Payment verified and order created successfully",
                true,
                finalOrder,
                HttpStatus.OK
            );
        } catch (IllegalStateException e) {
            // Payment already processed
            return ResponseUtil.buildResponse(
                e.getMessage(),
                false,
                null,
                HttpStatus.CONFLICT
            );
        } catch (Exception e) {
            // Mark payment as failed if order creation fails
            String orderId = verificationDTO != null ? verificationDTO.getRazorpay_order_id() : null;
            if (orderId != null) {
                try {
                    paymentSecurityService.incrementVerificationAttempts(orderId);
                    paymentService.markFailed(orderId, "Order creation failed: " + e.getMessage());
                } catch (Exception ex) {
                    // Log error but don't fail the response
                }
            }
            throw new CustomeRuntimeExceptionsHandler(
                "Error verifying payment: " + e.getMessage()
            );
        }
    }

    /**
     * Alternative endpoint that accepts payment verification data as separate parameters
     * and order request in body. Useful for frontend that sends payment data separately.
     */
    @PostMapping("/verify-legacy")
    public ResponseEntity<Map<String, Object>> verifyPaymentLegacy(
            @RequestParam String razorpay_order_id,
            @RequestParam String razorpay_payment_id,
            @RequestParam String razorpay_signature,
            @Valid @RequestBody OrderRequestDTO orderRequestDTO,
            HttpServletRequest request) {
        try {
            PaymentVerificationDTO verificationDTO = new PaymentVerificationDTO();
            verificationDTO.setRazorpay_order_id(razorpay_order_id);
            verificationDTO.setRazorpay_payment_id(razorpay_payment_id);
            verificationDTO.setRazorpay_signature(razorpay_signature);
            verificationDTO.setOrderRequest(orderRequestDTO);

            return verifyPayment(verificationDTO, request);
        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler(
                "Error verifying payment: " + e.getMessage()
            );
        }
    }

    /**
     * Get payment status by Razorpay order ID
     */
    @GetMapping("/status/{razorpayOrderId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(
            @PathVariable String razorpayOrderId,
            HttpServletRequest request) {
        try {
            String token = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);

            return paymentService.getPaymentByOrderId(razorpayOrderId)
                .map(payment -> {
                    // Verify user owns this payment
                    if (!payment.getUserId().equals(userId)) {
                        return ResponseUtil.buildResponse(
                            "Unauthorized access to payment",
                            false,
                            null,
                            HttpStatus.FORBIDDEN
                        );
                    }
                    return ResponseUtil.buildResponse(
                        "Payment status retrieved successfully",
                        true,
                        payment,
                        HttpStatus.OK
                    );
                })
                .orElse(ResponseUtil.buildResponse(
                    "Payment not found",
                    false,
                    null,
                    HttpStatus.NOT_FOUND
                ));
        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler(
                "Error retrieving payment status: " + e.getMessage()
            );
        }
    }

    /**
     * Razorpay Webhook endpoint for payment status updates
     * This endpoint should be publicly accessible (configured in SecurityConfig)
     * and should verify the webhook signature before processing
     * 
     * Production-ready webhook with proper signature verification and idempotency
     */
    @PostMapping(PAYMENT_WEBHOOK)
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestBody String webhookBody,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestHeader(value = "X-Razorpay-Event-Id", required = false) String eventId,
            HttpServletRequest request) {
        try {
            // ✅ SECURITY: Verify webhook signature
            if (signature == null || signature.isEmpty()) {
                log.warn("⚠️ Missing webhook signature from IP: {}", getClientIp(request));
                return ResponseUtil.buildResponse(
                    "Missing webhook signature",
                    false,
                    null,
                    HttpStatus.UNAUTHORIZED
                );
            }

            boolean isValid = paymentService.verifyWebhookSignature(webhookBody, signature);
            
            if (!isValid) {
                log.warn("⚠️ Invalid webhook signature received from IP: {}", getClientIp(request));
                return ResponseUtil.buildResponse(
                    "Invalid webhook signature",
                    false,
                    null,
                    HttpStatus.OK
                );
            }

            // Parse webhook event
            JSONObject event = new JSONObject(webhookBody);
            String eventType = event.getString("event");
            JSONObject payload = event.getJSONObject("payload");

            // ✅ IDEMPOTENCY: Check if webhook already processed using X-Razorpay-Event-Id
            if (eventId != null && !eventId.isEmpty()) {
                String webhookIdempotencyKey = "webhook:event:" + eventId;
                if (paymentSecurityService.isDuplicatePayment(webhookIdempotencyKey)) {
                    log.info("ℹ️ Webhook event {} already processed, skipping", eventId);
                    return ResponseUtil.buildResponse(
                        "Webhook already processed",
                        true,
                        null,
                        HttpStatus.OK
                    );
                }
            } else {
                // Fallback: use event id from body if header not present
                String bodyEventId = event.optString("id", "");
                if (!bodyEventId.isEmpty()) {
                    String webhookIdempotencyKey = "webhook:event:" + bodyEventId;
                    if (paymentSecurityService.isDuplicatePayment(webhookIdempotencyKey)) {
                        log.info("ℹ️ Webhook event {} already processed, skipping", bodyEventId);
                        return ResponseUtil.buildResponse(
                            "Webhook already processed",
                            true,
                            null,
                            HttpStatus.OK
                        );
                    }
                }
            }

            // Handle different event types
            boolean processed = false;
            switch (eventType) {
                case "payment.captured":
                    processed = handlePaymentCaptured(payload);
                    break;
                case "payment.failed":
                    processed = handlePaymentFailed(payload);
                    break;
                case "order.paid":
                    processed = handleOrderPaid(payload);
                    break;
                case "payment.authorized":
                    // Payment authorized but not captured yet
                    log.info("ℹ️ Payment authorized event received");
                    processed = true;
                    break;
                default:
                    log.info("ℹ️ Unhandled webhook event type: {}", eventType);
                    processed = true; // Acknowledge even if not handled
                    break;
            }

            if (processed) {
                return ResponseUtil.buildResponse(
                    "Webhook processed successfully",
                    true,
                    null,
                    HttpStatus.OK
                );
            } else {
                return ResponseUtil.buildResponse(
                    "Webhook received but processing failed",
                    false,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        } catch (Exception e) {
            log.error("❌ Error processing webhook: {}", e.getMessage(), e);
            throw new CustomeRuntimeExceptionsHandler(
                "Error processing webhook: " + e.getMessage()
            );
        }
    }

    private boolean handlePaymentCaptured(JSONObject payload) {
        try {
            JSONObject paymentEntity = payload.getJSONObject("payment").getJSONObject("entity");
            String orderId = paymentEntity.getString("order_id");
            String paymentId = paymentEntity.getString("id");
            String signature = paymentEntity.optString("signature", "");
            Double amount = paymentEntity.optDouble("amount", 0) / 100.0; // Convert from paise to rupees

            Optional<Payment> paymentOpt = paymentService.getPaymentByOrderId(orderId);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                
                // Only update if not already successful
                if (!"SUCCESS".equals(payment.getStatus())) {
                    // Validate amount if payment record exists
                    if (payment.getAmount() != null) {
                        double tolerance = payment.getAmount() * 0.01; // 1% tolerance
                        if (Math.abs(payment.getAmount() - amount) > tolerance) {
                            log.error("❌ Amount mismatch in webhook. Stored: {}, Received: {}", 
                                    payment.getAmount(), amount);
                            paymentService.markFailed(orderId, "Amount mismatch in webhook");
                            return false;
                        }
                    }
                    
                    // Mark success (orderId will be set when order is created via verify endpoint)
                    paymentService.markSuccess(orderId, paymentId, signature, payment.getOrderId());
                    log.info("✅ Payment captured via webhook: {}", orderId);
                } else {
                    log.info("ℹ️ Payment {} already marked as success", orderId);
                }
                return true;
            } else {
                log.warn("⚠️ Payment record not found for webhook order: {}", orderId);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error handling payment.captured webhook: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean handlePaymentFailed(JSONObject payload) {
        try {
            JSONObject paymentEntity = payload.getJSONObject("payment").getJSONObject("entity");
            String orderId = paymentEntity.getString("order_id");
            String errorCode = paymentEntity.optString("error_code", "");
            String errorDescription = paymentEntity.optString("error_description", "Payment failed");
            String errorReason = paymentEntity.optString("error_reason", "");

            String failureReason = String.format("Code: %s, Reason: %s, Description: %s", 
                    errorCode, errorReason, errorDescription);

            Optional<Payment> paymentOpt = paymentService.getPaymentByOrderId(orderId);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                if (!"FAILED".equals(payment.getStatus()) && !"SUCCESS".equals(payment.getStatus())) {
                    paymentService.markFailed(orderId, failureReason);
                    log.info("❌ Payment failed via webhook: {}", orderId);
                }
                return true;
            } else {
                log.warn("⚠️ Payment record not found for failed webhook order: {}", orderId);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error handling payment.failed webhook: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean handleOrderPaid(JSONObject payload) {
        try {
            // Order.paid event - typically handled by payment.captured
            // But we can log it for audit purposes
            JSONObject orderEntity = payload.getJSONObject("order").getJSONObject("entity");
            String orderId = orderEntity.getString("id");
            log.info("ℹ️ Order.paid event received for: {}", orderId);
            return true;
        } catch (Exception e) {
            log.error("❌ Error handling order.paid webhook: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs (X-Forwarded-For can contain multiple IPs)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}
