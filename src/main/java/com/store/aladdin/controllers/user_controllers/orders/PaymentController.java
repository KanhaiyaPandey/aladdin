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
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.store.aladdin.routes.UserRoutes.*;
import static com.store.aladdin.routes.AuthRoutes.*;


@RestController
@RequestMapping(USER_BASE)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    /**
     * Step 1: Create Razorpay Order
     * This endpoint creates a payment order in Razorpay and returns the order details
     * to the frontend for initiating the payment.
     */
    @PostMapping(RAZORPAY_CREATE_ORDER)
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestParam double amount,
            HttpServletRequest request) {
        try {
            // Extract user ID from JWT token
            String token = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);

            // Validate amount
            if (amount <= 0) {
                return ResponseUtil.buildResponse(
                    "Invalid amount. Amount must be greater than 0",
                    false,
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }

            // Create Razorpay order
            JSONObject razorpayOrder = paymentService.createOrder(amount, userId);

            // Convert JSONObject to Map for response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", razorpayOrder.getString("id"));
            response.put("amount", razorpayOrder.getInt("amount"));
            response.put("currency", razorpayOrder.getString("currency"));
            response.put("status", razorpayOrder.getString("status"));
            response.put("receipt", razorpayOrder.optString("receipt", ""));

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

            // Validate that order request is provided
            if (orderRequestDTO == null) {
                return ResponseUtil.buildResponse(
                    "Order details are required to complete the order",
                    false,
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }

            // Verify payment signature
            boolean isValid = paymentService.verifySignature(orderId, paymentId, signature);

            if (!isValid) {
                // Mark payment as failed
                paymentService.markFailed(orderId, "Signature verification failed");
                return ResponseUtil.buildResponse(
                    "Payment verification failed: Invalid signature",
                    false,
                    null,
                    HttpStatus.BAD_REQUEST
                );
            }

            // Update payment status in DB
            Payment payment = paymentService.markSuccess(orderId, paymentId, signature);

            // Verify that the payment belongs to the user
            if (!payment.getUserId().equals(userId)) {
                return ResponseUtil.buildResponse(
                    "Unauthorized: Payment does not belong to this user",
                    false,
                    null,
                    HttpStatus.FORBIDDEN
                );
            }

            // Create the actual order in the system
            Order finalOrder = orderService.createOrder(orderRequestDTO, userId);

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
        } catch (Exception e) {
            // Mark payment as failed if order creation fails
            String orderId = verificationDTO != null ? verificationDTO.getRazorpay_order_id() : null;
            if (orderId != null) {
                try {
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
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestBody String webhookBody,
            @RequestHeader("X-Razorpay-Signature") String signature,
            HttpServletRequest request) {
        try {
            // Verify webhook signature
            boolean isValid = paymentService.verifyWebhookSignature(webhookBody, signature);
            
            if (!isValid) {
                return ResponseUtil.buildResponse(
                    "Invalid webhook signature",
                    false,
                    null,
                    HttpStatus.UNAUTHORIZED
                );
            }

            // Parse webhook event
            JSONObject event = new JSONObject(webhookBody);
            String eventType = event.getString("event");
            JSONObject payload = event.getJSONObject("payload");

            // Handle different event types
            switch (eventType) {
                case "payment.captured":
                    handlePaymentCaptured(payload);
                    break;
                case "payment.failed":
                    handlePaymentFailed(payload);
                    break;
                case "order.paid":
                    handleOrderPaid(payload);
                    break;
                default:
                    // Log unhandled event types
                    break;
            }

            return ResponseUtil.buildResponse(
                "Webhook processed successfully",
                true,
                null,
                HttpStatus.OK
            );
        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler(
                "Error processing webhook: " + e.getMessage()
            );
        }
    }

    private void handlePaymentCaptured(JSONObject payload) {
        try {
            JSONObject paymentEntity = payload.getJSONObject("payment").getJSONObject("entity");
            String orderId = paymentEntity.getString("order_id");
            String paymentId = paymentEntity.getString("id");
            String signature = paymentEntity.optString("signature", "");

            Optional<Payment> paymentOpt = paymentService.getPaymentByOrderId(orderId);
            if (paymentOpt.isPresent()) {
                paymentService.markSuccess(orderId, paymentId, signature);
            }
        } catch (Exception e) {
            // Log error but don't fail webhook processing
        }
    }

    private void handlePaymentFailed(JSONObject payload) {
        try {
            JSONObject paymentEntity = payload.getJSONObject("payment").getJSONObject("entity");
            String orderId = paymentEntity.getString("order_id");
            String errorDescription = paymentEntity.optString("error_description", "Payment failed");

            Optional<Payment> paymentOpt = paymentService.getPaymentByOrderId(orderId);
            if (paymentOpt.isPresent()) {
                paymentService.markFailed(orderId, errorDescription);
            }
        } catch (Exception e) {
            // Log error but don't fail webhook processing
        }
    }

    private void handleOrderPaid(JSONObject payload) {
        // Handle order.paid event if needed
        // This is typically handled by payment.captured event
    }

}
