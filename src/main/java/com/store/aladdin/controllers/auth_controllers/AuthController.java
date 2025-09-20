package com.store.aladdin.controllers.auth_controllers;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.store.aladdin.routes.AuthRoutes.*;

import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.MailService;
import com.store.aladdin.utils.validation.UserValidation;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final MailService mailService;
    private final AuthService authService;

    private static final String TYPE = "JWT_TOKEN";
    private static final String SET = "Set-Cookie";

    @Value("${app.cookie.secure}")
    private boolean secure;

    // login
    @PostMapping(LOGIN_ROUTE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthPojo loginUser, HttpServletResponse response) {

        // Fetch user by email
        User user = userService.getUserByEmail(loginUser.getEmail());
        if (user == null) {
            return ResponseUtil.buildResponse("User not found", HttpStatus.BAD_REQUEST);
        }
        User logedinUser = userService.authenticateUser(loginUser.getEmail(), loginUser.getPassword());
        if (logedinUser != null) {
            authService.set_cookie(logedinUser, response);
//            mailService.sendEmail(logedinUser.getEmail(), "testing mail", "mail working fine");
            return ResponseUtil.buildResponse("Login successful", HttpStatus.OK);
        }
          return ResponseUtil.buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
    


    // register
        @PostMapping(REGISTER_ROUTE)
        public ResponseEntity<Map<String, Object>> createUser(@RequestBody String userJson, HttpServletResponse response) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                User user = objectMapper.readValue(userJson, User.class);
                String validationMessage = userValidation.validateUser(user);
                if (validationMessage != null) {
                    return ResponseUtil.buildResponse(validationMessage, HttpStatus.BAD_REQUEST);
                }
                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                    user.setRoles(List.of("USER"));
                }
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);
                userService.createUser(user);
                authService.set_cookie(user, response);
                return ResponseUtil.buildResponse("User registered and logged in successfully", HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        // validate token

        @GetMapping(VALIDATION_ROUTE)
        public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        try {
        String token = authService.getToken(request);
        if (token == null) {
            return ResponseUtil.buildResponse("Token not found", HttpStatus.UNAUTHORIZED);
        }
            String email = JwtUtil.validateToken(token);
            if (email == null) {
                return ResponseUtil.buildResponse("Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseUtil.buildResponse("User not found", HttpStatus.UNAUTHORIZED);
            }
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("roles", user.getRoles());
            userInfo.put("createdAt", user.getCreatedAt());
            userInfo.put("updatedAt",user.getUpdatedAt());
            userInfo.put("profilePicture", user.getProfilePicture());
            userInfo.put("userId", user.getId().toString());
            return ResponseUtil.buildResponse("Token is valid", true, userInfo, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Invalid token", HttpStatus.UNAUTHORIZED);
            }

        }

        // Logout

        @PostMapping(LOGOUT_ROUTE)
        public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
            authService.remove_cookie(response);
            return ResponseUtil.buildResponse("Logged out successfully", HttpStatus.OK);
        }

}