package com.store.aladdin.services;

import com.store.aladdin.models.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import com.store.aladdin.repository.OrderRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public Order createOrder(Order order){
        order.setCreatedAt(LocalDateTime.now());
         return orderRepository.save(order);
    }


}
