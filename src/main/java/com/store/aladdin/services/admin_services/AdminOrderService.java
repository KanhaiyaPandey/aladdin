package com.store.aladdin.services.admin_services;

import com.store.aladdin.models.Order;
import com.store.aladdin.repository.OrderRepository;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.helper.Enums;
import com.store.aladdin.validations.OrderValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderValidation orderValidation;
    private final ProductRepository productRepository;

    public List<Order> updateOrderStatus(List<String> orderIds, String status){
        if (orderIds == null || orderIds.isEmpty()) {
            throw new IllegalArgumentException("Order IDs cannot be empty");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        List<Order> orders = orderRepository.findAllById(orderIds);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for given IDs");
        }

        for (Order order : orders.parallelStream().toList()) {
            order.setStatus(Enums.OrderStatus.valueOf(status.toUpperCase()));
            order.setUpdatedAt(LocalDateTime.now());
        }

        return orderRepository.saveAll(orders);
    }

}
