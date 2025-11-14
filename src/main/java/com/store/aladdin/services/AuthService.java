package com.store.aladdin.services;

import com.store.aladdin.models.User;
import com.store.aladdin.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String TYPE = "JWT_TOKEN";
    private static final String SET = "Set-Cookie";

    @Value("${app.cookie.secure}")
    private boolean secure;

    public void setCookie(User user, HttpServletResponse response) {
        String token = JwtUtil.generateToken(user);
        int maxAge = 60 * 60 * 24;
        String cookie = String.format(
                "JWT_TOKEN=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=%d",
                token, maxAge
        );
        response.setHeader("Set-Cookie", cookie);
    }

    public void removeCookie(HttpServletResponse response){
        Cookie cookie = new Cookie(TYPE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        response.setHeader(SET, "JWT_TOKEN=; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=0");
    }


    public String getToken(HttpServletRequest request) {
    if (request.getCookies() == null) return null;

    for (Cookie cookie : request.getCookies()) {
        if (cookie.getName().equals(TYPE)) {
            return cookie.getValue();
        }
    }
    return null;  // don't throw here
}

    
   
}
