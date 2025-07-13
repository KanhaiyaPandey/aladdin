package com.store.aladdin.filters;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.store.aladdin.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        // Extract JWT token from a custom cookie
        String token = Arrays.stream(request.getCookies() == null ? new Cookie[0] : request.getCookies())
                .filter(cookie -> "JWT_TOKEN".equals(cookie.getName())) 
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (token != null) {
            try {
                // System.out.println("JWT Token: " + token);
                String username = JwtUtil.validateToken(token); 

                // Extract roles without adding "ROLE_"
                String[] roles = JwtUtil.extractRoles(token); // No need to add "ROLE_" here

                List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // ensures ROLE_ prefix
                    .toList();

                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            username,
                            "",
                            authorities
                    );

                // if (username != null) {
                //     System.out.println("Authenticated user: " + username);
                //     System.out.println("Roles: " + Arrays.toString(userDetails.getAuthorities().toArray()));
                // }

                // System.out.println("JWT Token found in cookies");

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
      
            } catch (RuntimeException e) {
                System.err.println("Error during token validation: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } 

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("null")
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/user/login") || path.startsWith("/api/public/");
    }
}
