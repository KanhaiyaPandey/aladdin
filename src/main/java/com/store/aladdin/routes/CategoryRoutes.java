package com.store.aladdin.routes;

import lombok.RequiredArgsConstructor;

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;

@RequiredArgsConstructor
public class CategoryRoutes {

    public static final String CATEGORY_BASE = ADMIN_BASE + "/category";
    public static final String CREATE_CATEGORY  =  "/create-category";
    public static final String UPDATE_CATEGORY  =  "/update-category/{categoryId}";
    public static final String DELETE_CATEGORY =  "/delete-categories";
    public static final String UPDATE_ATTRIBUTE =  "/update-attribute/{attributeId}";
    public static final String CREATE_ATTRIBUTE =  "/create-attribute";
    public static final String DELETE_ATTRIBUTES =  "/delete-attributes";


}
