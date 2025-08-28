package com.store.aladdin.services;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.store.aladdin.dtos.OrderFilterDto;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Order;
import com.store.aladdin.queries.OrderQueries;
import com.store.aladdin.utils.helper.Enums.OrderStatus;

import lombok.RequiredArgsConstructor;

import com.store.aladdin.repository.OrderRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


}
