package com.store.aladdin.filters;

import org.jetbrains.annotations.NotNull;
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

import static com.store.aladdin.security.CookieService.ACCESS_COOKIE;

/**
 * Authenticates each request off the single ACCESS_TOKEN cookie (Path=/,
 * see CookieService). There's no separate admin-vs-user cookie anymore -
 * roles live inside the token itself, and admin-only routes are enforced by
 * SecurityConfig's hasRole("ADMIN") on /api/aladdin/admin/**, the same as
 * any other Spring Security role check.
 *
 * On a missing/invalid/expired token this filter just leaves the request
 * unauthenticated and moves on - it does NOT attempt to refresh. Refreshing
 * is the frontend's job (see utils/authRefresh.js): it calls
 * POST /api/auth/refresh on a 401 and retries.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException, ServletException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = Arrays.stream(cookies)
                .filter(c -> ACCESS_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (token != null) {
            try {
                String username = JwtUtil.validateToken(token);
                String[] roles = JwtUtil.extractRoles(token);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        username,
                        "",
                        authorities
                );

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
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
