package com.store.aladdin.services;

import com.store.aladdin.models.User;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.validation.UserValidation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidation userValidation;
    private final MailService mailService;

    private static final String TYPE = "JWT_TOKEN";
    private static final String SET = "Set-Cookie";

    @Value("${app.cookie.secure}")
    private boolean secure;

    public void setCookie(User logedinUser, HttpServletResponse response){
        String token = JwtUtil.generateToken(logedinUser);
        Cookie cookie = new Cookie(TYPE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.setHeader(SET, "JWT_TOKEN=" + token +
                "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=86400");
        response.addCookie(cookie);
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
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TYPE.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null || token.isBlank()) {
            throw new AccessDeniedException("Token missing or invalid");
        }
        return token;
    }


}
