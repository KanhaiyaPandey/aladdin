package com.store.aladdin.routes;


public class AuthRoutes {

    public static final String AUTH_BASE = "/api/auth/user";
    public static final String LOGIN_ROUTE = "/login";
    public static final String LOGOUT_ROUTE = "/logout";
    public static final String REGISTER_ROUTE = "/register";
    public static final String VALIDATION_ROUTE = "/validate-token";

    public static final String GOOGLEAUTH_ROUTE = "/google/login";


    public static final String ADMIN_BASE = "/api/aladdin/admin";
    public static final String ADMIN_VALIDATION_ROUTE = "/validate-token";
    public static final String USER_BASE = "/api/aladdin/user";
    public static final String USER_VALIDATION_ROUTE = "/validate-token";
    public static final String PUBLIC_BASE = "/api/aladdin/public";

    private AuthRoutes() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }
}
