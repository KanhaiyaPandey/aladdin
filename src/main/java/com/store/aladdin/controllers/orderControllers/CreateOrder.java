package com.store.aladdin.controllers.orderControllers;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.Order;
import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.helper.TokenUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class CreateOrder {

    @Autowired
    private UserService userService;

    
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
        @RequestBody String orderJson,
        HttpServletRequest request
    ) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            ObjectId userId = TokenUtil.extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseUtil.buildResponse("Unauthorized: Missing token", HttpStatus.UNAUTHORIZED);
            }
            order.setUserId(userId.toHexString());
            Order savedOrder =  userService.createOrder(order);
            User userOptional = userService.getUserById(userId);
            userOptional.getOrders().add(savedOrder);
            userService.updateUser(userId, userOptional);     
            return ResponseUtil.buildResponse("Order creation successfull.", true, savedOrder, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseUtil.buildResponse("Failed to create order: " + e.getMessage(), false, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


