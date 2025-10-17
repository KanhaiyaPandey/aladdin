package com.store.aladdin.routes;

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;

public class WarehouseRoutes {

    public static final String WAREHOUSE_BASE = ADMIN_BASE + "/warehouse";
    public static final String CREATE_WAREHOUSE = "/create-warehouse";
    public static final String UPDATE_WAREHOUSE = "/update-warehouse/{id}";
    public static final String SINGLE_WAREHOUSE = "/{id}";
    public static final String GER_ALL_WAREHOUSE = "/all";
}
