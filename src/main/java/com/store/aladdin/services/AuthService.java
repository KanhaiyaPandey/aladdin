package com.store.aladdin.services;

import com.store.aladdin.security.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Thin facade kept around because a number of resource controllers
 * (OrderControllers, UserControllers, PaymentController, ...) are injected
 * with AuthService and call getToken(request) to identify the current user
 * from the access-token cookie. Cookie building/clearing and refresh-token
 * handling now live in com.store.aladdin.security (CookieService,
 * RefreshTokenService, TokenService) - this class no longer does either.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CookieService cookieService;

    public String getToken(HttpServletRequest request) {
        return cookieService.readAccessToken(request);
    }
}
