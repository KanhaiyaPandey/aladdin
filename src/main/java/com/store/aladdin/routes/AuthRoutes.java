package com.store.aladdin.routes;


public class AuthRoutes {

    // All auth endpoints (login/register/logout/me/refresh/google) live under
    // one base. This used to be "/api/auth/user" - one character away from
    // USER_BASE ("/api/aladdin/user") below, which is exactly what caused the
    // JWT cookie Path mismatch bug: the cookie was scoped to USER_BASE while
    // the frontend was reading it from AUTH_BASE. Keeping these visibly
    // distinct (no shared "/user" suffix) removes that whole failure mode.
    public static final String AUTH_BASE = "/api/auth";
    public static final String LOGIN_ROUTE = "/login";
    public static final String LOGOUT_ROUTE = "/logout";
    public static final String REGISTER_ROUTE = "/register";
    public static final String ME_ROUTE = "/me";
    public static final String REFRESH_ROUTE = "/refresh";

    public static final String GOOGLEAUTH_ROUTE = "/google/login";

    // Resource route bases - unrelated to auth, left exactly as they were.
    public static final String ADMIN_BASE = "/api/aladdin/admin";
    public static final String USER_BASE = "/api/aladdin/user";
    public static final String PUBLIC_BASE = "/api/aladdin/public";

    private AuthRoutes() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }
}
