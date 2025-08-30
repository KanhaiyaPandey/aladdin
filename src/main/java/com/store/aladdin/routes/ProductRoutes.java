package com.store.aladdin.routes;


import lombok.RequiredArgsConstructor;

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;

@RequiredArgsConstructor
public class ProductRoutes {
    public static final String PRODUCT_BASE   =  ADMIN_BASE + "/product";
    public static final String CREATE_PRODUCT =  "/create-product";
    public static final String UPDATE_PRODUCT =  "/update-product/{productId}";
    public static final String DELETE_PRODUCT =  "/delete-product/{productId}";
}
