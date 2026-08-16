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

        // Path is deliberately "/" for both cookies, NOT scoped to ADMIN_BASE/
        // USER_BASE: which cookie (by name) is trusted for a given request is
        // already decided in application code by JwtAuthFilter/getToken() below
        // based on the request path, so a narrower browser-side Path here does
        // no extra security work - it only risks the browser withholding the
        // cookie on a legitimate same-app request that lives under a different
        // path prefix (e.g. AUTH_BASE's /validate-token, which isn't under
        // USER_BASE). It also has to match clearCookie()'s Path exactly, or
        // logout silently fails to remove it.
        String cookie = String.format(
                "%s=%s; Path=/; HttpOnly; %sSameSite=%s; Max-Age=%d",
                cookieName,
                token,
                secure ? "Secure; " : "",
                secure ? "None" : "Lax",
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
        cookie.setSecure(secure);
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
