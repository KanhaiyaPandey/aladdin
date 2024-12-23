package com.store.aladdin.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity; consider enabling in production
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll() // Public routes are open
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin routes require ADMIN role
                .requestMatchers("/api/user/**").authenticated() // User routes require authentication
                .anyRequest().denyAll() // Deny all other requests
            )
            .httpBasic();

        return http.build();
    }

}



