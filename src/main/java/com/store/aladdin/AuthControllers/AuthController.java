package com.store.aladdin.AuthControllers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import com.store.aladdin.utils.validation.ValidationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class AuthController {

     @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final String type = "JWT_TOKEN";


    // login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser, HttpServletResponse response) {
        // Fetch user by email
        User user = userService.getUserByEmail(loginUser.getEmail());
        if (user == null) {
            return ResponseUtil.buildResponse("User not found", HttpStatus.BAD_REQUEST);
        }

                    User logedinUser = userService.authenticateUser(loginUser.getEmail(), loginUser.getPassword());
    
        // Check if password matches
        if (logedinUser != null) {
            // Retrieve roles for the user
            // List<String> roles = userService.getUserRoles(loginUser.getEmail());
    
            // Generate JWT token with username and roles
            String token = JwtUtil.generateToken(logedinUser);
    
            // Add JWT token as a cookie
            Cookie cookie = new Cookie(type, token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // ✅ Required for SameSite=None to work on HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); // 1 day
            // Java's Cookie API doesn't support SameSite directly — override via header:
            response.setHeader("Set-Cookie", "JWT_TOKEN=" + token +
                "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=86400"); 
            // Optionally add the cookie (redundant but okay)
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
        public ResponseEntity<?> createUser(@RequestBody String userJson, HttpServletResponse response) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // Hash the password before storing

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
                Cookie cookie = new Cookie(type, token);
                cookie.setHttpOnly(true);
                cookie.setSecure(false); // Set true in production
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24); // 1 day
                response.addCookie(cookie);
                response.setHeader("Set-Cookie", "JWT_TOKEN=" + token + "; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=86400");

                return ResponseUtil.buildResponse("User registered and logged in successfully", HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        // validate token


        
        @GetMapping("/validate-token")
        public ResponseEntity<?> validateToken(HttpServletRequest request) {

        try {
        String token = null;

        // Extract token from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (type.equals(cookie.getName())) {
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
        public ResponseEntity<?> logout(HttpServletResponse response) {
            // Clear the JWT cookie
            Cookie cookie = new Cookie(type, null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Set to false if not using HTTPS locally
            cookie.setPath("/");
            cookie.setMaxAge(0); // Deletes the cookie immediately
            response.addCookie(cookie);
            response.setHeader("Set-Cookie", "JWT_TOKEN=; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=0");
            return ResponseUtil.buildResponse("Logged out successfully", true, null , HttpStatus.OK);
        }

}