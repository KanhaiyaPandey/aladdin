package com.store.aladdin.routes.admin_routes;

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;

public class AdminOrderRoutes {

    public static final String ORDER_BASE     =   ADMIN_BASE + "/order";
    public static final String CREATE_MANUAL_ORDER   =   "/create-manual-order";

    public static final String ALL_ORDERS     =   "/all-orders";
    public static final String UPDATE_ORDERS  =   "/update-orders";
    public static final String DELETE_ORDERS  =   "/delete-orders";
    public static final String SINGLE_ORDER   =   "/{id}";



}
