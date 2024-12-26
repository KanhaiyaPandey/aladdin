package com.store.aladdin.configs;

import com.store.aladdin.filters.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/login").permitAll() // Allow login without authentication
                .requestMatchers("/api/public/**").permitAll() // Allow public routes
                .requestMatchers("/api/user/**").authenticated() // Secure user routes
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Role check expects ROLE_ADMIN internally
                .anyRequest().denyAll() // Deny all other requests
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
            )
            .addFilterBefore(new JwtAuthFilter(), 
                UsernamePasswordAuthenticationFilter.class); // Add JWT filter before default filter
    
        return http.build();
    }
}
