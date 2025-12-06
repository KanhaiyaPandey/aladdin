package com.store.aladdin.routes;

public class UserRoutes {

    public static final String UPDATE_USER = "/update-profile";
    public static final String ADD_ADDRESS =  "/add-address";
    public static final String UPDATE_ADDRESS = "/address/{addressId}";
    public static final String DELETE_ADDRESS = "/address/{addressId}";
    public static final String ALL_ADDRESSES =  "/address";
    public static final String SET_ADDRESS_DEFAULT = "/address/{addressId}/default";
    public static final String MY_ORDERS   = "/orders/my-orders";
    public static final String UPDATE_CART = "/update-cart";

    public static final String RAZORPAY_CREATE_ORDER = "/payment/create-order";
    public static final String VARIFY_PAYMENT        = "/payment/verify";
    public static final String PAYMENT_WEBHOOK       = "/payment/webhook";
    public static final String PAYMENT_STATUS        = "/payment/status/{razorpayOrderId}";

}
