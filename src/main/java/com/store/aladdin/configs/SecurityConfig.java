
package com.store.aladdin.configs;


import com.store.aladdin.filters.JwtAuthFilter;
import com.store.aladdin.exceptions.CustomAccessDeniedHandler;

import java.util.Arrays;
import java.util.List;

import static com.store.aladdin.routes.AuthRoutes.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // Inject CustomAccessDeniedHandler into the configuration
    public SecurityConfig(CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
                    // Open authentication endpoints
                    .requestMatchers(AUTH_BASE + LOGIN_ROUTE).permitAll()
                    .requestMatchers(AUTH_BASE + REGISTER_ROUTE).permitAll()
                    .requestMatchers(AUTH_BASE + VALIDATION_ROUTE).permitAll()

                    // Public API is open
                    .requestMatchers(PUBLIC_BASE + "/**").permitAll()

                    // Admin can access everything
                    .requestMatchers(ADMIN_BASE + "/**").hasRole("ADMIN")
                    .requestMatchers(USER_BASE + "/**").authenticated()
                    .anyRequest().denyAll()
            )
            .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler) // Use custom handler for 403 Forbidden
            .and()
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
            )
            .addFilterBefore(new JwtAuthFilter(), 
                UsernamePasswordAuthenticationFilter.class); // Add JWT filter before default filter
    
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173","http://localhost:5174, https://aladdin01.netlify.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (cookies)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints
        return source;
    }
}