package com.store.aladdin.AuthControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.ResponseUtil;
import com.store.aladdin.utils.ValidationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class AuthController {

     @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // login

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser, HttpServletResponse response) {
        // Fetch user by email
        User user = userService.getUserByEmail(loginUser.getEmail());
        if (user == null) {
            return ResponseUtil.buildResponse("User not found", HttpStatus.BAD_REQUEST);
        }
    
        // Check if password matches
        if (userService.authenticateUser(loginUser.getEmail(), loginUser.getPassword())) {
            // Retrieve roles for the user
            List<String> roles = userService.getUserRoles(loginUser.getEmail());
    
            // Generate JWT token with username and roles
            String token = JwtUtil.generateToken(user.getEmail(), roles);
    
            // Add JWT token as a cookie
            Cookie cookie = new Cookie("JWT_TOKEN", token); // Ensure name matches JwtAuthFilter
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Set true for production with HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); // 1 day
            response.addCookie(cookie);
    
            return ResponseUtil.buildResponse("Login successful", HttpStatus.OK);
        } else {
            return ResponseUtil.buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
    


// register

        @PostMapping("/register")
        public ResponseEntity<?> createUser(@RequestBody User user) {
            String validationMessage = ValidationUtils.validateUser(user);
            if (validationMessage != null) {
                return ResponseUtil.buildResponse(validationMessage, HttpStatus.BAD_REQUEST);
            }

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(List.of("USER"));
            }

            try {
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);
                userService.createUser(user);
                return ResponseUtil.buildResponse("User created successfully", HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

}