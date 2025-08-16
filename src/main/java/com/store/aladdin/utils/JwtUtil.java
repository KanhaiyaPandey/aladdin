package com.store.aladdin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component  
public class JwtUtil {

    private static String secretKey;

    @Value("${spring.data.secretkey}")
    public void setSecretKey(String secret) {
        JwtUtil.secretKey = secret;
    }

    private static final long EXPIRATION_TIME = 86400000;  

    // Generate JWT token with username and roles
    public static String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getEmail()) 
                .withClaim("userId", user.getId().toString()) 
                .withClaim("roles", String.join(",", user.getRoles()))  
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  
                .sign(Algorithm.HMAC256(secretKey));
    }

        // Validate JWT token and extract the subject (username)
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

    

    // Extract roles from the JWT token (no "ROLE_" prefix)
    public static String[] extractRoles(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey))
                    .build()
                    .verify(token);
            String rolesClaim = decodedJWT.getClaim("roles").asString();
            return rolesClaim.split(",");
        } catch (JWTVerificationException e) {
            throw new CustomeRuntimeExceptionsHandler("Unable to extract roles from token", e);
        }
    }

    public static String extractUserId(String token) {
    return JWT.require(Algorithm.HMAC256(secretKey))
            .build()
            .verify(token)
            .getClaim("userId")
            .asString();
    }
}
