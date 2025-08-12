package com.store.aladdin.controllers.payment;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.store.aladdin.DTOs.OrderResponseTest;
import com.store.aladdin.services.PaymentService;
import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/api/public/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;

    @Value("${razorpay.api.secret}")
    private String rezSecret;


@PostMapping("/create-payment")
public ResponseEntity<?> createOrder(@RequestParam int amount, @RequestParam String currency) {
    try {
        OrderResponseTest order = paymentService.createOrder(amount, currency, "recepient_100");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("order", order);
        response.put("message", "Order created successfully");

        if (Boolean.TRUE.equals(response.get("success"))) {
            OrderResponseTest orderFromMap = (OrderResponseTest) response.get("order");
            System.out.println("Order ID: " + orderFromMap.getId());
        }


        return ResponseEntity.ok(response);

    } catch (RazorpayException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Failed to create order");
        error.put("error", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}


    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> paymentData) {
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        String razorpayPaymentId = paymentData.get("razorpay_payment_id");
        String razorpaySignature = paymentData.get("razorpay_signature");

        String secret = rezSecret; 

        String generatedSignature = HmacSHA256(razorpayOrderId + "|" + razorpayPaymentId, secret);

        if (generatedSignature.equals(razorpaySignature)) {
            return ResponseUtil.buildResponse("Payment verified successfully.", true, null, HttpStatus.OK);
        } else {
            return ResponseUtil.buildResponse("Invalid signature. Payment verification failed.", false, null, HttpStatus.BAD_REQUEST);
        }
    }



    public static String HmacSHA256(String data, String secret) {
    try {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hashBytes = mac.doFinal(data.getBytes());
        return Hex.encodeHexString(hashBytes);
    } catch (Exception e) {
        throw new RuntimeException("Unable to generate HMAC", e);
    }
}




}
