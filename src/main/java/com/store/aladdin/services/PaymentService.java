package com.store.aladdin.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class PaymentService {

        @Value("${razorpay.api.key}")
        private String apiKey;

        @Value("${razorpay.api.secret}")
        private String apiSecret;
}
