package com.store.aladdin.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.store.aladdin.dtos.OrderResponseTest;

@Service
public class PaymentService {

        @Value("${razorpay.api.key}")
        private String apiKey;

        @Value("${razorpay.api.secret}")
        private String apiSecret;
}
