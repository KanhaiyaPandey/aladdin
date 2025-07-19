package com.store.aladdin.controllers.storeControllers.storeData;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/store")
public class SetStoreData {

    @PostMapping(value = "/set-store-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setStoreData(){
      return null;
    }
    
}
