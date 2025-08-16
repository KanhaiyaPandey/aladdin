package com.store.aladdin.controllers.auth_controllers;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.dtos.AuthPojo;
import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import com.store.aladdin.utils.validation.ValidationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String TYPE = "JWT_TOKEN";
    private static final String SET = "Set-Cookie";

    // login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthPojo loginUser, HttpServletResponse response) {

        // Fetch user by email
        User user = userService.getUserByEmail(loginUser.getEmail());
  
        if (user == null) {
            return ResponseUtil.buildResponse("User not found", HttpStatus.BAD_REQUEST);
        }

                    User logedinUser = userService.authenticateUser(loginUser.getEmail(), loginUser.getPassword());
    
        if (logedinUser != null) {

            String token = JwtUtil.generateToken(logedinUser);
            Cookie cookie = new Cookie(SET, token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); 
            response.setHeader(SET, "JWT_TOKEN=" + token +
                "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=86400"); 
            response.addCookie(cookie);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", logedinUser.getName());
            userInfo.put("email", logedinUser.getEmail());
            userInfo.put("roles", logedinUser.getRoles());
            return ResponseUtil.buildResponse("Login successful", true ,userInfo , HttpStatus.OK);
        } else {
            return ResponseUtil.buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
    


    // register
        @PostMapping("/register")
        public ResponseEntity<Map<String, Object>> createUser(@RequestBody String userJson, HttpServletResponse response) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {

                User user = objectMapper.readValue(userJson, User.class);
                String validationMessage = ValidationUtils.validateUser(user);
                
                if (validationMessage != null) {
                    return ResponseUtil.buildResponse(validationMessage, HttpStatus.BAD_REQUEST);
                }

                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                    user.setRoles(List.of("USER"));
                }

                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);

                // Save user to the database
                userService.createUser(user);

                // Generate JWT token
                String token = JwtUtil.generateToken(user);

                // Set the token in an HTTP-only cookie
                Cookie cookie = new Cookie(TYPE, token);
                cookie.setHttpOnly(true);
                cookie.setSecure(false); 
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24); 
                response.addCookie(cookie);
                response.setHeader(SET, "JWT_TOKEN=" + token + "; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=86400");

                return ResponseUtil.buildResponse("User registered and logged in successfully", HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        // validate token

        @GetMapping("/validate-token")
        public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {

        try {
        String token = null;

        // Extract token from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TYPE.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            return ResponseUtil.buildResponse("Token not found", HttpStatus.UNAUTHORIZED);
        }

            // Validate and parse the token
            String email = JwtUtil.validateToken(token);
            if (email == null) {
                return ResponseUtil.buildResponse("Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseUtil.buildResponse("User not found", HttpStatus.UNAUTHORIZED);
            }

            // Return user info
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("roles", user.getRoles());

            return ResponseUtil.buildResponse("Token is valid", true, userInfo, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Invalid token", HttpStatus.UNAUTHORIZED);
            }

        }



        
        @PostMapping("/logout")
        public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
            // Clear the JWT cookie
            Cookie cookie = new Cookie(TYPE, null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); 
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            response.setHeader(SET, "JWT_TOKEN=; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=0");
            return ResponseUtil.buildResponse("Logged out successfully", true, null , HttpStatus.OK);
        }

}