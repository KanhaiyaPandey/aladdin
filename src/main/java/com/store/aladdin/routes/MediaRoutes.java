package com.store.aladdin.routes;

import static com.store.aladdin.routes.AuthRoutes.*;


public class MediaRoutes {
    public static final String MEDIA_BASE    =  ADMIN_BASE + "/media";
    public static final String UPLOAD_MEDIA  =  "/upload-media";

    private MediaRoutes() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }
}
