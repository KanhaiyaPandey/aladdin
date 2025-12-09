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

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;
import static com.store.aladdin.routes.AuthRoutes.USER_BASE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    @Value("${app.cookie.secure}")
    private boolean secure;

    private static final String USER_COOKIE = "USER_JWT";
    private static final String ADMIN_COOKIE = "ADMIN_JWT";

    private static final int ONE_DAY = 60 * 60 * 24;


    // ---------------------------
    // SET COOKIE
    // ---------------------------
    public void setCookie(User user, HttpServletResponse response) {
        String token = JwtUtil.generateToken(user);

        boolean isAdmin = user.getRoles().contains("ADMIN");

        String cookieName = isAdmin ? ADMIN_COOKIE : USER_COOKIE;
        String cookiePath = isAdmin ? ADMIN_BASE : USER_BASE;

        String cookie = String.format(
                "%s=%s; Path=%s; HttpOnly; Secure; SameSite=None; Max-Age=%d",
                cookieName,
                token,
                cookiePath,
                ONE_DAY
        );

        response.addHeader("Set-Cookie", cookie);
        log.info("Setting cookie {} for user {}", cookieName, user.getEmail());
    }


    // ---------------------------
    // REMOVE COOKIE (LOGOUT)
    // ---------------------------
    public void removeCookie(HttpServletResponse response) {

        clearCookie("USER_JWT", response);
        clearCookie("ADMIN_JWT", response);
    }

    private void clearCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0); // delete cookie
        response.addCookie(cookie);
    }


    // ---------------------------
    // READ TOKEN BASED ON ROUTE
    // ---------------------------
    public String getToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;
        String path = request.getRequestURI();

        // Admin request → read ADMIN_JWT
        if (path.startsWith(ADMIN_BASE)) {
            return getCookieValue(request, ADMIN_COOKIE);
        }

        // User request → read USER_JWT
        if (path.startsWith(USER_BASE)) {
            return getCookieValue(request, USER_COOKIE);
        }

        // Public request → no token needed
        return null;
    }


    // ---------------------------
    // HELPER — Extract cookie by name
    // ---------------------------
    private String getCookieValue(HttpServletRequest request, String name) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(name)) {
                return c.getValue();
            }
        }
        return null;
    }
}
