package com.store.aladdin.security;

import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The one place login/register/OAuth-success/refresh/logout go through to
 * issue or revoke the access+refresh cookie pair. Replaces the old
 * AuthService.setCookie()/removeCookie() (which built cookies by hand and
 * only ever dealt with a single, non-revocable 24h token).
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;
    private final UserService userService;

    /** Login / register / OAuth success: mint a brand new session for this user. */
    public void issueTokens(User user, HttpServletResponse response) {
        String accessToken = JwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.issue(user.getId());

        cookieService.setAccessCookie(response, accessToken);
        cookieService.setRefreshCookie(response, refreshToken);
    }

    /**
     * Rotates the refresh token found in the request and issues a fresh
     * access+refresh pair. Throws InvalidRefreshTokenException (missing,
     * expired, or reused) if the session can't be continued - callers should
     * clear cookies and respond 401 in that case.
     */
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String presented = cookieService.readRefreshToken(request);
        RefreshTokenService.RotationResult result = refreshTokenService.rotate(presented);

        // Re-fetch the user rather than trusting anything from the old
        // access token - picks up role changes and catches deleted accounts.
        User user = userService.getUserById(result.userId());

        String newAccessToken = JwtUtil.generateToken(user);
        cookieService.setAccessCookie(response, newAccessToken);
        cookieService.setRefreshCookie(response, result.refreshToken());
    }

    /** Logout: revoke the whole session family server-side and clear both cookies. */
    public void revoke(HttpServletRequest request, HttpServletResponse response) {
        String presented = cookieService.readRefreshToken(request);
        refreshTokenService.revoke(presented);

        cookieService.clearAccessCookie(response);
        cookieService.clearRefreshCookie(response);
    }

    public void clearCookies(HttpServletResponse response) {
        cookieService.clearAccessCookie(response);
        cookieService.clearRefreshCookie(response);
    }
}
