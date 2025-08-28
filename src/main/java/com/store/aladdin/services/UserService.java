package com.store.aladdin.services;

import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Order;
import com.store.aladdin.models.Product;
import com.store.aladdin.models.User;
import com.store.aladdin.repository.OrderRepository;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.repository.UserRepository;
import com.store.aladdin.utils.CartItem;
import com.store.aladdin.utils.CartResponseItem;
import com.store.aladdin.utils.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;


    // Create a new user
    public void createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    // Get a user by ID
    public User getUserById(ObjectId userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }

    // find by email
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
    }


    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }


    // Delete a user
    public void deleteUser(ObjectId userId) {
        userRepository.deleteById(userId);
    }

    private boolean productExists(String productId) {
        // Logic to check if product exists in the database
        return productRepository.existsById(productId);
    }


    // Remove a product from the user's cart
    public void removeFromCart(ObjectId userId, ObjectId productId) {
        User user = getUserById(userId);
        user.getCart().removeIf(cartItem -> cartItem.getProductId().equals(productId));
        userRepository.save(user);
    }


    // Get user's cart
    public List<CartResponseItem> getUserCart(ObjectId userId) {
        User user = getUserById(userId);
        List<CartResponseItem> cartResponse = new ArrayList<>();

        for (CartItem item : user.getCart()) {
            Product product = productRepository.findById(item.getProductId().toString())
                    .orElseThrow(() -> new CustomeRuntimeExceptionsHandler("product not found"));
            CartResponseItem responseItem = new CartResponseItem(
                    item.getProductId(),
                    product.getTitle(),
                    product.getDescription(),
                    product.getSellPrice(),
                    item.getQuantity()
            );

            cartResponse.add(responseItem);
        }

        return cartResponse;
    }


}
