package com.store.aladdin.controllers.AdminController.orders;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Order;
import com.store.aladdin.services.OrderService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class OrderControllers {


    private final OrderService orderService;


    @GetMapping("/get-orders")
    public ResponseEntity<?> getOrders (
        @RequestParam(required = false) String userName,
        @RequestParam(required = false) String minPrice,
        @RequestParam(required = false) String maxPrice,
        @RequestParam(required = false) String paymentStatus,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) LocalDateTime startDate,
        @RequestParam(required = false) LocalDateTime endDate,
        @RequestParam(required = false) LocalDateTime deliveryStartDate,
        @RequestParam(required = false) LocalDateTime deliveryEndDate,
        @RequestParam(required = false) String pincode,
        @RequestParam(required = false) String orderId,
        @RequestParam(required = false) String userId
    ){

     List <Order> orders = orderService.getOrders(userName, minPrice, maxPrice, paymentStatus, status, startDate, endDate, deliveryStartDate, deliveryEndDate, pincode, orderId, userId);
    return ResponseUtil.buildResponse("Orders fetched successfully.", true, orders, HttpStatus.OK);
 


    }
    
}
