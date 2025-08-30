package com.store.aladdin.routes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthRoutes {

    public static final String AUTH_BASE = "/api/auth/user";
    public static final String LOGIN_ROUTE = "/login";
    public static final String LOGOUT_ROUTE = "/logout";
    public static final String REGISTER_ROUTE = "/register";
    public static final String VALIDATION_ROUTE = "/validate-token";


    public static final String ADMIN_BASE = "/api/aladdin/admin";
    public static final String USER_BASE = "/api/aladdin/user";
    public static final String PUBLIC_BASE = "/api/aladdin/public";


}
