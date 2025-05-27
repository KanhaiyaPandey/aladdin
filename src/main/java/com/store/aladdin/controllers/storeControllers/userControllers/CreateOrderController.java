// package com.store.aladdin.controllers.storeControllers.userControllers;


// import org.bson.types.ObjectId;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.store.aladdin.models.Order;
// import com.store.aladdin.models.Order.OrderStatus;
// import com.store.aladdin.models.User;
// import com.store.aladdin.services.UserService;
// import com.store.aladdin.utils.response.ResponseUtil;

// @RestController
// @RequestMapping("/api/user/orders")
// public class CreateOrderController {

//     @Autowired
//     private UserService userService;

//     @PostMapping("/create/{userId}")
//     public ResponseEntity<?> createOrder(@PathVariable ObjectId userId, @RequestBody Order order) {

//         // if (order.getStatus() == null) {
//         //     order.setStatus(OrderStatus.PROCESSING);
//         // }

//         order.setUserId(userId.toHexString());
//         Order savedOrder =  userService.createOrder(order);

//         User userOptional = userService.getUserById(userId);
//         userOptional.getOrders().add(savedOrder);
//         userService.updateUser(userId, userOptional);


//         return ResponseUtil.buildResponse("Order created successfully", HttpStatus.OK);
//     }
// }
