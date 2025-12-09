package com.store.aladdin.controllers.admincontroller.orders;

import com.store.aladdin.models.Order;
import com.store.aladdin.services.OrderService;
import com.store.aladdin.utils.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.store.aladdin.routes.OrderRoutes.*;

@RestController
@RequestMapping(ADMIN_ORDER_BASE)
@RequiredArgsConstructor
public class OrdersControllers {

    private final OrderService orderService;

    @GetMapping(ADMIN_GET_ALL_ORDERS)
    public ResponseEntity<Map<String, Object>> getAllOrders(){
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseUtil.buildResponse("Orders fetched successfully", true, orders, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
