package com.store.aladdin.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

/**
 * The only place in the app that builds or reads the two auth cookies.
 * Built on Spring's {@link ResponseCookie}, not hand-formatted header
 * strings - the previous approach was the direct cause of a Set-Cookie
 * Path mismatch bug (cookie set with one Path, read with another) that
 * took an entire debugging session to track down. Centralizing "set" and
 * "clear" here so they always agree on Path/Domain/SameSite by construction.
 */
@Component
public class CookieService {

    public static final String ACCESS_COOKIE = "ACCESS_TOKEN";
    public static final String REFRESH_COOKIE = "REFRESH_TOKEN";

    // Access token cookie has to be readable on every request (resource
    // routes under /api/aladdin/**, auth routes under /api/auth/**), so it
    // stays Path=/. The refresh token is only ever needed by /api/auth/refresh
    // and /api/auth/logout, so it's scoped narrower - it simply won't be sent
    // on ordinary API calls, which is one less place it can leak from.
    private static final String ACCESS_PATH = "/";
    private static final String REFRESH_PATH = "/api/auth";

    @Value("${app.cookie.secure}")
    private boolean secure;

    @Value("${app.jwt.access-ttl-minutes}")
    private long accessTtlMinutes;

    @Value("${app.jwt.refresh-ttl-days}")
    private long refreshTtlDays;

    public void setAccessCookie(HttpServletResponse response, String token) {
        addCookie(response, build(ACCESS_COOKIE, token, ACCESS_PATH, Duration.ofMinutes(accessTtlMinutes)));
    }

    public void setRefreshCookie(HttpServletResponse response, String token) {
        addCookie(response, build(REFRESH_COOKIE, token, REFRESH_PATH, Duration.ofDays(refreshTtlDays)));
    }

    public void clearAccessCookie(HttpServletResponse response) {
        addCookie(response, build(ACCESS_COOKIE, "", ACCESS_PATH, Duration.ZERO));
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        addCookie(response, build(REFRESH_COOKIE, "", REFRESH_PATH, Duration.ZERO));
    }

    public String readAccessToken(HttpServletRequest request) {
        return readCookie(request, ACCESS_COOKIE);
    }

    public String readRefreshToken(HttpServletRequest request) {
        return readCookie(request, REFRESH_COOKIE);
    }

    private ResponseCookie build(String name, String value, String path, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .maxAge(maxAge);

        // SameSite=None is required for the deployed cross-site setup
        // (vercel.app frontend <-> onrender.com backend) and is only valid
        // together with Secure. Over plain http on localhost, secure is false
        // and we fall back to Lax so the cookie still gets stored/sent.
        builder.sameSite(secure ? "None" : "Lax");
        return builder.build();
    }

    private void addCookie(HttpServletResponse response, ResponseCookie cookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
