package com.store.aladdin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.store.aladdin.models.Order;
import com.store.aladdin.models.Order.OrderStatus;
import com.store.aladdin.repository.OrderRepository;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> updateOrderStatus(List<String> orderIds, String status) {

        
        List<ObjectId> objectIdList = orderIds.stream()
                .map(ObjectId::new)
                .collect(Collectors.toList());


        List<Order> orders = orderRepository.findAllById(objectIdList);
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        for (Order order : orders) {
            order.setStatus(orderStatus);
        }
        return orderRepository.saveAll(orders);
    }
}
