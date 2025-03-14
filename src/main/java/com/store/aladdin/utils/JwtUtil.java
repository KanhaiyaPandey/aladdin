package com.store.aladdin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component  // Register JwtUtil as a Spring-managed bean
public class JwtUtil {

    private static String SECRET_KEY;

    @Value("${spring.data.secretkey}")
    public void setSecretKey(String secretKey) {
        JwtUtil.SECRET_KEY = secretKey;
    }

    // private static final String SECRET_KEY = "your-secret-key";  // Should be stored securely
    private static final long EXPIRATION_TIME = 86400000;  // 1 day in ms

    // Generate JWT token with username and roles
    public static String generateToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)  // Set the username (email) as the subject
                .withClaim("roles", String.join(",", roles))  // Set the roles as a comma-separated string
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Set expiration time
                .sign(Algorithm.HMAC256(SECRET_KEY));  // Sign the token with HMAC256
    }

    // Validate JWT token and extract the subject (username)
    public static String validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token)
                    .getSubject();  // Extract the username (email) from the token
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

    // Extract roles from the JWT token (no "ROLE_" prefix)
    public static String[] extractRoles(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            String rolesClaim = decodedJWT.getClaim("roles").asString();
            // Split roles and return without adding "ROLE_" prefix
            return rolesClaim.split(",");
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Unable to extract roles from token", e);
        }
    }
}
