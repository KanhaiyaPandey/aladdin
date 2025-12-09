package com.store.aladdin.controllers.auth_controllers;

import com.store.aladdin.dtos.UserResponseDTO;
import com.store.aladdin.models.User;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.store.aladdin.routes.AuthRoutes.*;


@RestController
@RequestMapping(USER_BASE)
@RequiredArgsConstructor
public class UserValidationController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping(USER_VALIDATION_ROUTE)
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {

        try {
            String token = authService.getToken(request);
            if (token == null) {
                return ResponseUtil.buildResponse("Token not found", HttpStatus.UNAUTHORIZED);
            }
            String id = JwtUtil.extractUserId(token);
            if (id == null) {
                return ResponseUtil.buildResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseUtil.buildResponse("User not found", HttpStatus.NOT_FOUND);
            }
            UserResponseDTO userResponseDTO = new UserResponseDTO(user, false);
            return ResponseUtil.buildResponse("Token is valid", true, userResponseDTO , HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildResponse("Invalid token", HttpStatus.UNAUTHORIZED);
        }

    }
}
