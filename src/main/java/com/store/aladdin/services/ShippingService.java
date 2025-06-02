package com.store.aladdin.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class ShippingService {

    @Value("${shiprocket.api.email}")
    private String email;

    @Value("${shiprocket.api.pass}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createToken() {
        String url = "https://apiv2.shiprocket.in/v1/external/auth/login";

        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", email);
        requestBody.put("password", password);

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request
        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // Send request
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject responseBody = new JSONObject(response.getBody());
            return responseBody.getString("token");
        } else {
            throw new RuntimeException("Failed to create Shiprocket token. Status: " + response.getStatusCode());
        }
    }

     public String createShipping(String order, String token) {
        String url = "https://apiv2.shiprocket.in/v1/external/orders/create/adhoc";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(order, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }




}

