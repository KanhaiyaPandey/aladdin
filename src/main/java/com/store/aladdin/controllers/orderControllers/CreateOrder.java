package com.store.aladdin.controllers.orderControllers;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.store.aladdin.utils.helper.Enums.OrderStatus;

@RestController
@RequestMapping("/api/user")
public class CreateOrder {

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/create-order/{userId}")
    public ResponseEntity<?> createOrder(
        @PathVariable ObjectId userId,
        @RequestBody Order order
    ) {

        order.setUserId(userId.toHexString());
        Order savedOrder =  userService.createOrder(order);

        User userOptional = userService.getUserById(userId);
        userOptional.getOrders().add(savedOrder);
        userService.updateUser(userId, userOptional);
        System.out.println("User ID: " + userId);
        System.out.println("Raw Order Data: " + order);     
        return ResponseEntity.ok("Order creation endpoint reached successfully.");
    }
}


