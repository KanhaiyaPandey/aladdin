package com.store.aladdin.routes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PublicRoutes {
    public static final String PUBLIC_BASE = "/api/aladdin/public";

    //    PRODUCT
    public static final String PUBLIC_ALL_PRODUCTS = "/product/all-products";
    public static final String PUBLIC_SINGLE_PRODUCT = "/product/{productId}";

    //    CATEGORY
    public static final String PUBLIC_ALL_CATEGORIES = "/category/all-categories";
    public static final String PUBLIC_SINGLE_CATEGORY = "/category/{id}";





}
