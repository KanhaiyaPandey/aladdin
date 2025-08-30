package com.store.aladdin.routes;


public class PublicRoutes {
    public static final String PUBLIC_BASE = "/api/aladdin/public";

    //    PRODUCT
    public static final String PUBLIC_ALL_PRODUCTS = "/product/all-products";
    public static final String PUBLIC_SINGLE_PRODUCT = "/product/{productId}";

    //    CATEGORY
    public static final String PUBLIC_ALL_CATEGORIES = "/category/all-categories";
    public static final String PUBLIC_SINGLE_CATEGORY = "/category/{id}";


    private PublicRoutes() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }


}
