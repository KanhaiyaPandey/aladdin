package com.store.aladdin.controllers.admincontroller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Store;
import com.store.aladdin.services.StoreService;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/aladdin/admin/store/settings")
@RequiredArgsConstructor
public class StoreAdminController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSettings() {
        try {
            Store store = storeService.getStoreSettings();
            return ResponseUtil.buildResponse("Store settings fetched", true, store, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error fetching settings", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody Store store) {
        try {
            Store updated = storeService.updateStoreSettings(store);
            return ResponseUtil.buildResponse("Store settings updated", true, updated, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error updating settings", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
