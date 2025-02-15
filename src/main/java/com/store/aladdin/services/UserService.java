package com.store.aladdin.services;

import com.store.aladdin.models.Order;
import com.store.aladdin.models.Product;
import com.store.aladdin.models.User;
import com.store.aladdin.repository.OrderRepository;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.repository.UserRepository;
import com.store.aladdin.utils.CartItem;
import com.store.aladdin.utils.CartResponseItem;
import com.store.aladdin.utils.ResourceNotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

      @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderRepository orderRepository;


    // Create a new user
    public void createUser(User user) {
        // Check for existing user by name or email
        Optional<User> existingUserByName = userRepository.findByName(user.getName());
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());

        if (existingUserByName.isPresent()) {
            throw new IllegalArgumentException("A user with this name already exists.");
        }
        if (existingUserByEmail.isPresent()) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
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

    public List<String> getUserRoles(String email) {
        User user = getUserByEmail(email);  
        return user.getRoles();  
    }

    public boolean authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        return passwordEncoder.matches(password, user.getPassword());  // Match the provided password with the stored one
    }

    // Update a user
    public void updateUser(ObjectId userId, User updatedUser) {
        User existingUser = getUserById(userId);
    
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword()); // Ensure hashing if necessary
        }

        if (updatedUser.getCart() != null && !updatedUser.getCart().isEmpty()) {
            existingUser.getCart().clear();
            existingUser.getCart().addAll(updatedUser.getCart());
        }

        if (updatedUser.getOrders() != null && !updatedUser.getOrders().isEmpty()) {
            existingUser.getOrders().clear();
            existingUser.getOrders().addAll(updatedUser.getOrders());
        }
            
    
        userRepository.save(existingUser);
    }
    

    // Delete a user
    public void deleteUser(ObjectId userId) {
        userRepository.deleteById(userId);
    }

   // Add a product to the user's cart
public void addToCart(ObjectId userId, CartItem item) {
    User user = getUserById(userId);
    
    // Check if the product exists
    if (!productExists(item.getProductId())) {
        throw new RuntimeException("Product does not exist");
    }

    // Find existing cart item for the product
    CartItem existingItem = null;
    for (CartItem cartItem : user.getCart()) {
        if (cartItem.getProductId().equals(item.getProductId())) {
            existingItem = cartItem;
            break;
        }
    }

    if (existingItem != null) {
        // If the product is already in the cart, increase the quantity
        existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
    } else {
        // Otherwise, create a new CartItem and add it to the cart
        user.getCart().add(item); // item already contains the productId and quantity
    }

    // Save the updated user back to the repository
    userRepository.save(user);
}

    
    private boolean productExists(ObjectId productId) {
        // Logic to check if product exists in the database
        return productRepository.existsById(productId);
    }
    

    
// Remove a product from the user's cart
public void removeFromCart(ObjectId userId, ObjectId productId) {
    User user = getUserById(userId);
    
    // Find and remove the CartItem with the matching productId
    user.getCart().removeIf(cartItem -> cartItem.getProductId().equals(productId));
    
    userRepository.save(user);
}



// Get user's cart
public List<CartResponseItem> getUserCart(ObjectId userId) {
    User user = getUserById(userId);
    List<CartResponseItem> cartResponse = new ArrayList<>();

    for (CartItem item : user.getCart()) {
        // Fetch product details for each productId
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

        // Create a CartResponseItem with both Product and CartItem details
        CartResponseItem responseItem = new CartResponseItem(
                item.getProductId(),
                product.getTitle(),
                product.getDescription(),
                product.getSellPrice(),
                product.getCompareAtPrice(),
                item.getQuantity()
        );

        cartResponse.add(responseItem);
    }

    return cartResponse;
}

// create order
   

   public Order createOrder (Order order){
   Order savedOrder =  orderRepository.save(order);
   return savedOrder;

   }



    // Get user's orders
    // public List<ObjectId> getUserOrders(ObjectId userId) {
    //     User user = getUserById(userId);
    //     return user.getOrders();
    // }
}
