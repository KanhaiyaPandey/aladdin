package com.store.aladdin.controllers.payment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.store.aladdin.DTOs.OrderResponseTest;
import com.store.aladdin.services.PaymentService;

@RestController
@RequestMapping("/api/public/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@PostMapping("/create-order")
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

}
