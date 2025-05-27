package com.store.aladdin.controllers.orderControllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.DTOs.OrderDTO;
import com.store.aladdin.models.Order;
import com.store.aladdin.models.User;
import com.store.aladdin.repository.OrderRepository;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.helper.Enums.OrderStatus;
import com.store.aladdin.utils.response.ResponseUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class CreateOrder {

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
        @RequestBody Order order,
        HttpServletRequest request
    ) {

        try {

            String token = Arrays.stream(request.getCookies())
                .filter(cookie -> "JWT_TOKEN".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        if (token == null) {
            return ResponseUtil.buildResponse("Unauthorized: Missing token", false, null, HttpStatus.UNAUTHORIZED);
        }

        // Extract userId from token
        String userIdStr = JwtUtil.extractUserId(token);
        ObjectId userId = new ObjectId(userIdStr);

            order.setUserId(userId.toHexString());
            Order savedOrder =  userService.createOrder(order);
            User userOptional = userService.getUserById(userId);
            userOptional.getOrders().add(savedOrder);
            userService.updateUser(userId, userOptional);     
            return ResponseUtil.buildResponse("Order creation successfull.", true, savedOrder, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseUtil.buildResponse("Failed to create order: " + e.getMessage(), false, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


