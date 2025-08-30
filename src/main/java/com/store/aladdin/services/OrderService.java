package com.store.aladdin.services;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import com.store.aladdin.repository.OrderRepository;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;


}
