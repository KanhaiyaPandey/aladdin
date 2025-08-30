package com.store.aladdin.routes;

import lombok.RequiredArgsConstructor;

import static com.store.aladdin.routes.AuthRoutes.*;

@RequiredArgsConstructor
public class MediaRoutes {
    public static final String MEDIA_BASE    =  ADMIN_BASE + "/media";
    public static final String UPLOAD_MEDIA  =  "/upload-media";
}
