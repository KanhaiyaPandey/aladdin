package com.store.aladdin.controllers.AdminController.shipping;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.services.OrderService;
import com.store.aladdin.services.ShippingService;
import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/api/admin/shipping")
public class CreateShipping {

    @Autowired
    private ShippingService shippingService;

    @Autowired OrderService orderService;

    
    @PostMapping(value = "/create-shipping", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createShipping(@RequestBody String orderPayload) {
        try {
            String token;
            try {
                token = shippingService.createToken();
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Failed to generate shipping token: " + e.getMessage(), false, null, HttpStatus.UNAUTHORIZED);
            }
            String responseJson;
            try {
                responseJson = shippingService.createShipping(orderPayload, token);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Failed to create shipping order: " + e.getMessage(), false, null, HttpStatus.BAD_GATEWAY);
            }
            Map<String, Object> shippingResponse;
            try {
                ObjectMapper mapper = new ObjectMapper();
                shippingResponse = mapper.readValue(responseJson, Map.class);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Failed to parse shipping response: " + e.getMessage(), false, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                orderService.updateShippingInfo(shippingResponse);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Failed to update order with shipping info: " + e.getMessage(), false, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseUtil.buildResponse("Shipping created successfully", true, shippingResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.buildResponse("Unexpected error occurred: " + e.getMessage(), false, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
}
