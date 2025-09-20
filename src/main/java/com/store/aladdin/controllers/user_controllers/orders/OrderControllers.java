package com.store.aladdin.controllers.user_controllers.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Order;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.OrderService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.store.aladdin.routes.OrderRoutes.*;

@RestController
@RequestMapping(USER_ORDER_BASE)
@RequiredArgsConstructor
public class OrderControllers {

    private final OrderService orderService;
    private final AuthService authService;

    @PostMapping(USER_CREATE_ORDER)
    public ResponseEntity<Map<String, Object>> createOrder (@RequestBody String reqJson, HttpServletRequest request) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(reqJson, Order.class);
            String token = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);
            order.setCustomerId(userId);
            Order saved_order = orderService.create_order(order);
            return ResponseUtil.buildResponse("order created successfully", true, saved_order, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler("oops! " + e.getMessage());
        }
    }

}
