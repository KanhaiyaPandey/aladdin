package com.store.aladdin.controllers.AdminController.shipping;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.services.ShippingService;
import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/api/public/shipping")
public class CreateShipping {

    @Autowired
    private ShippingService shippingService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping(value = "/create-shipping", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createShipping(@RequestBody String orderPayload){
    try {
        String token = shippingService.createToken();
        String responseJson = shippingService.createShipping(orderPayload, token);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> shippingResponse = mapper.readValue(responseJson, Map.class);

        return ResponseUtil.buildResponse("Shipping created successfully",true, shippingResponse, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace(); // Optional: log to a logger instead of console
        return ResponseUtil.buildResponse("Failed to create shipping", HttpStatus.INTERNAL_SERVER_ERROR);
    }
 }
    
}
