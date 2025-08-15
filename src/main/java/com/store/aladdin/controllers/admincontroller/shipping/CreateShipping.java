package com.store.aladdin.controllers.admincontroller.shipping;

import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.store.aladdin.services.ShippingService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/shipping")
@RequiredArgsConstructor
public class CreateShipping {

    private final ShippingService shippingService;

    
    @PostMapping(value = "/create-shipping", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createShipping(@RequestBody String orderPayload) {
    try {
        String token = generateShippingToken();
        
        String responseJson;
        try {
            responseJson = shippingService.createShipping(orderPayload, token);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(
                "Failed to create shipping order: " + e.getMessage(),
                false,
                null,
                HttpStatus.BAD_GATEWAY
            );
        }

        return ResponseUtil.buildResponse("Shipping created successfully", true, responseJson, HttpStatus.OK);

    } catch (Exception e) {
        return ResponseUtil.buildResponse(
            "Unexpected error: " + e.getMessage(),
            false,
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

private String generateShippingToken() {
    try {
        return shippingService.createToken();
    } catch (Exception e) {
        throw new RuntimeException("Failed to generate shipping token: " + e.getMessage());
    }
}


    
}
