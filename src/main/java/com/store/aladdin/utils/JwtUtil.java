package com.store.aladdin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static String secretKey;

    @Value("${spring.data.secretkey}")
    private String secret;

    @Bean
    public void configureJwtUtil() {
        JwtUtil.secretKey = secret;
    }

    private static final long EXPIRATION_TIME = 86400000;

    // Generate JWT token with roles stored as JSON array
    public static String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId().toString())
                .withClaim("roles", user.getRoles())  // <-- FIXED: store as array
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(secretKey));
    }

    // Validate JWT token & extract subject (email)
    public static String validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new CustomeRuntimeExceptionsHandler("Invalid or expired token");
        }
    }

    // Extract roles safely (supports array OR string)
    public static String[] extractRoles(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token);

            // Try as List<String>
            List<String> rolesList = jwt.getClaim("roles").asList(String.class);
            if (rolesList != null && !rolesList.isEmpty()) {
                return rolesList.toArray(new String[0]);
            }

            // Try as String
            String rolesString = jwt.getClaim("roles").asString();
            if (rolesString != null && !rolesString.isBlank()) {
                return rolesString.split(",");
            }

            return new String[]{};

        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler("Unable to extract roles from token", e);
        }
    }

    // Extract userId
    public static String extractUserId(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getClaim("userId")
                .asString();
    }
}
