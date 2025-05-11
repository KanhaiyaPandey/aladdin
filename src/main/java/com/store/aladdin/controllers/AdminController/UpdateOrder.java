package com.store.aladdin.controllers.AdminController;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Order;
import com.store.aladdin.services.OrderService;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/admin")
public class UpdateOrder {

    @Autowired
    private OrderService orderService;

    @PostMapping("/update-order-status")
    public ResponseEntity<?> updateOrderStatus(@RequestBody Map<String, Object> requestBody) {

            List<String> orderIds = (List<String>) requestBody.get("orderIds");
            String status = (String) requestBody.get("status");
            if (orderIds == null || orderIds.isEmpty() || status == null || status.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request data");
            }
            List<Order> updatedOrders = orderService.updateOrderStatus(orderIds, status);
            return ResponseUtil.buildResponse("Order status updated successfully", true ,updatedOrders, HttpStatus.OK);
            
    }
}
