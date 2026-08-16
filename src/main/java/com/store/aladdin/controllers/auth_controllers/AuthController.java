package com.store.aladdin.controllers.auth_controllers;



import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.store.aladdin.routes.AuthRoutes.*;
import static com.store.aladdin.utils.helper.Enums.RiskStatus.LOW;

import com.store.aladdin.dtos.UserResponseDTO;
import com.store.aladdin.security.InvalidRefreshTokenException;
import com.store.aladdin.security.TokenService;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.validations.UserValidation;
import lombok.extern.slf4j.Slf4j;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Single canonical home for every auth endpoint. Used to be split across
 * this controller plus two near-identical copies of "validate the token"
 * (UserValidationController, AdminVarificationControllers) - collapsed here
 * into one /me, since admin-vs-user is a role check, not a different route.
 */
@Slf4j
@RestController
@RequestMapping(AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final AuthService authService;
    private final TokenService tokenService;

    // login
    @PostMapping(LOGIN_ROUTE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthPojo loginUser, HttpServletResponse response) {
        User user = userService.getUserByEmail(loginUser.getEmail());
        if (user == null) {
            return ResponseUtil.buildResponse("User not found", HttpStatus.BAD_REQUEST);
        }
        User logedinUser = userService.authenticateUser(loginUser.getEmail(), loginUser.getPassword());
        if (logedinUser != null) {
            tokenService.issueTokens(logedinUser, response);
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
                user.setRiskStatus(LOW);
                userService.createUser(user);
                tokenService.issueTokens(user, response);
                return ResponseUtil.buildResponse("User registered and logged in successfully", HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }



            @GetMapping(GOOGLEAUTH_ROUTE)
            public void googleLoginRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
                String redirectTo = request.getParameter("redirectTo");
                // Only accept same-site relative paths here - this value gets echoed
                // back into a redirect Location header by CustomOAuth2SuccessHandler
                // once Google sends the user back, so it must never be allowed to
                // point off-site.
                if (redirectTo != null && redirectTo.startsWith("/")) {
                    request.getSession().setAttribute("redirect_uri", redirectTo);
                }
                response.sendRedirect("/oauth2/authorization/google");
            }



        // Who is the current user, based on the access-token cookie. The one
        // and only "am I logged in" endpoint - both the storefront and the
        // admin dashboard call this (the dashboard additionally checks
        // data.roles for ADMIN client-side).
        @GetMapping(ME_ROUTE)
        public ResponseEntity<Map<String, Object>> me(HttpServletRequest request) {
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
                boolean isAdmin = user.getRoles().contains("ADMIN");
                UserResponseDTO userResponseDTO = new UserResponseDTO(user, isAdmin);
                return ResponseUtil.buildResponse("Token is valid", true, userResponseDTO, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseUtil.buildResponse("Invalid token", HttpStatus.UNAUTHORIZED);
            }
        }

        // Silent refresh: exchanges the (rotated) refresh cookie for a new
        // access+refresh pair. Called by the frontend's axios interceptor
        // (utils/authRefresh.js) whenever a request comes back 401.
        @PostMapping(REFRESH_ROUTE)
        public ResponseEntity<Map<String, Object>> refresh(HttpServletRequest request, HttpServletResponse response) {
            try {
                tokenService.refresh(request, response);
                return ResponseUtil.buildResponse("Session refreshed", HttpStatus.OK);
            } catch (InvalidRefreshTokenException e) {
                tokenService.clearCookies(response);
                return ResponseUtil.buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }

        // Logout
        @PostMapping(LOGOUT_ROUTE)
        public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
            tokenService.revoke(request, response);
            return ResponseUtil.buildResponse("Logged out successfully", true, null, HttpStatus.OK);
        }

}
