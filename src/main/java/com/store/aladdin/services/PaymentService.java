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

    public OrderResponseTest createOrder(int amount, String currency) throws RazorpayException  {
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100);
        orderRequest.put("currency", currency);
        Order order = razorpayClient.orders.create(orderRequest);

        OrderResponseTest response = new OrderResponseTest();
        response.setId(order.get("id"));
        response.setAmount(order.get("amount"));
        response.setCurrency(order.get("currency"));

        return response;
    }
}
