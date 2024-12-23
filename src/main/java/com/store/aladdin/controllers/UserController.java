package com.store.aladdin.controllers;

import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.CartItem;
import com.store.aladdin.utils.CartResponseItem;
import com.store.aladdin.utils.ResponseUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;





    // Get a user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable ObjectId userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // Update user
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable ObjectId userId, @RequestBody User user) {
        userService.updateUser(userId, user);
        return ResponseUtil.buildResponse("User updated successfully", HttpStatus.OK);
    }

    // Delete user
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable ObjectId userId) {
        userService.deleteUser(userId);
        return ResponseUtil.buildResponse("User deleted successfully", HttpStatus.OK);
    }

    // Add product to cart
    @PostMapping("/{userId}/cart/add")
    public ResponseEntity<?> addToCart(@PathVariable ObjectId userId, @RequestBody CartItem item) {
        // Ensure the item does not have an ID when being added
        item.setId(null); // Set ID to null to avoid mapping issues
        userService.addToCart(userId, item);
        return ResponseEntity.ok("Product added to cart successfully");
    }
    

    // Remove product from cart
    @DeleteMapping("/{userId}/cart/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable ObjectId userId, @PathVariable ObjectId productId) {
        userService.removeFromCart(userId, productId);
        return ResponseUtil.buildResponse("Product removed from cart", HttpStatus.OK);
    }

    // Get user's cart
 @GetMapping("/{userId}/cart")
public ResponseEntity<?> getUserCart(@PathVariable ObjectId userId) {
    List<CartResponseItem> cart = userService.getUserCart(userId);
    
    if (cart.isEmpty()) {
        return ResponseEntity.ok("Cart is empty");
    }

    return ResponseEntity.ok(cart);
}



    // Get user's orders
    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> getUserOrders(@PathVariable ObjectId userId) {
        List<ObjectId> orders = userService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }
}
