package com.store.aladdin.controllers;

import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.CartResponseItem;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

   
    private final UserService userService;


    // Delete user
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable ObjectId userId) {
        userService.deleteUser(userId);
        return ResponseUtil.buildResponse("User deleted successfully", HttpStatus.OK);
    }

    // Add product to cart
    // @PostMapping("/{userId}/cart/add")
    // public ResponseEntity<Map<String, Object>> addToCart(@PathVariable ObjectId userId, @RequestBody CartItem item) {
    //     item.setId(null);
    //     userService.addToCart(userId, item);
    //     return ResponseEntity.ok("Product added to cart successfully");
    // }
    

    // Remove product from cart
    @DeleteMapping("/{userId}/cart/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable ObjectId userId, @PathVariable ObjectId productId) {
        userService.removeFromCart(userId, productId);
        return ResponseUtil.buildResponse("Product removed from cart", HttpStatus.OK);
    }

    // Get user's cart
 @GetMapping("/{userId}/cart")
public ResponseEntity<Map<String, Object>> getUserCart(@PathVariable ObjectId userId) {
    List<CartResponseItem> cart = userService.getUserCart(userId);
    
    if (cart.isEmpty()) {
        return ResponseUtil.buildResponse("cart is empty", HttpStatus.OK);
    }

    return ResponseUtil.buildResponse("cart fetched", true, cart, HttpStatus.OK);
}



    // // Get user's orders
    // @GetMapping("/{userId}/orders")
    // public ResponseEntity<Map<String, Object>> getUserOrders(@PathVariable ObjectId userId) {
    //     List<ObjectId> orders = userService.getUserOrders(userId);
    //     return ResponseEntity.ok(orders);
    // }
}
