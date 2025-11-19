package com.store.aladdin.services;


import com.store.aladdin.dtos.UserResponseDTO;
import com.store.aladdin.models.User;
import com.store.aladdin.repository.UserRepository;

import com.store.aladdin.utils.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;


    // Create a new user
    public void createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    public void saveUserByOauth(String email, String name, HttpServletResponse response, String picture){
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setName(name);
        user.setProfilePicture(picture);
        user.setRoles(List.of("USER"));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User registeredUser = userRepository.save(user);
        authService.setCookie(registeredUser, response);
    }


    // Get a user by ID
    public User getUserById(String userId) {
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

    public UserResponseDTO updateUser(User user){
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BeanUtils.copyProperties(user, existingUser);
        User saved = userRepository.save(existingUser);
        return new UserResponseDTO(saved);
    }




    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }


    // Delete a user
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    // Remove a product from the user's cart
    // public void removeFromCart(String userId, String productId) {
    //     User user = getUserById(userId);
    //     user.getCart().removeIf(cartItem -> cartItem.getProductId().equals(productId));
    //     userRepository.save(user);
    // }


    // Get user's cart
    // public List<CartResponseItem> getUserCart(ObjectId userId) {
    //     User user = getUserById(userId);
    //     List<CartResponseItem> cartResponse = new ArrayList<>();

    //     for (CartItem item : user.getCart()) {
    //         Product product = productRepository.findById(item.getProductId().toString())
    //                 .orElseThrow(() -> new CustomeRuntimeExceptionsHandler("product not found"));
    //         CartResponseItem responseItem = new CartResponseItem(
    //                 item.getProductId(),
    //                 product.getTitle(),
    //                 product.getDescription(),
    //                 product.getSellPrice(),
    //                 item.getQuantity()
    //         );

    //         cartResponse.add(responseItem);
    //     }

    //     return cartResponse;
    // }


}
