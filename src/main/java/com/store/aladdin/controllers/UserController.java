package com.store.aladdin.controllers;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

   
    // private final UserService userService;


    // Delete user
    // @DeleteMapping("/delete/{userId}")
    // public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable ObjectId userId) {
    //     userService.deleteUser(userId);
    //     return ResponseUtil.buildResponse("User deleted successfully", HttpStatus.OK);
    // }


    // Remove product from cart
    // @DeleteMapping("/{userId}/cart/remove/{productId}")
    // public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable ObjectId userId, @PathVariable ObjectId productId) {
    //     userService.removeFromCart(userId, productId);
    //     return ResponseUtil.buildResponse("Product removed from cart", HttpStatus.OK);
    // }

    // Get user's cart
//  @GetMapping("/{userId}/cart")
// public ResponseEntity<Map<String, Object>> getUserCart(@PathVariable ObjectId userId) {
//     List<CartResponseItem> cart = userService.getUserCart(userId);
    
//     if (cart.isEmpty()) {
//         return ResponseUtil.buildResponse("cart is empty", HttpStatus.OK);
//     }

//     return ResponseUtil.buildResponse("cart fetched", true, cart, HttpStatus.OK);
// }

}
