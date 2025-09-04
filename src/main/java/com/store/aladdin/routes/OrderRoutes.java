package com.store.aladdin.routes;

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;
import static com.store.aladdin.routes.AuthRoutes.USER_BASE;

public final class OrderRoutes {

    private OrderRoutes() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    // =========================
    // Admin Order Routes
    // =========================
    public static final String ADMIN_ORDER_BASE       = ADMIN_BASE + "/orders";
    public static final String ADMIN_CREATE_ORDER     = "/create";
    public static final String ADMIN_GET_ALL_ORDERS   = "/all";
    public static final String ADMIN_GET_ORDER_BY_ID  = "/{orderId}";
    public static final String ADMIN_UPDATE_ORDER     = "/{orderId}/update";
    public static final String ADMIN_DELETE_ORDER     = "/{orderId}/delete";  // optional

    // =========================
    // User Order Routes
    // =========================
    public static final String USER_ORDER_BASE        = USER_BASE + "/orders";
    public static final String USER_CREATE_ORDER      = "/create";
    public static final String USER_GET_MY_ORDERS     = "/my";
    public static final String USER_GET_ORDER_BY_ID   = "/{orderId}";
    public static final String USER_CANCEL_ORDER      = "/{orderId}/cancel";  // optional
}
