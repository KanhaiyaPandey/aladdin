package com.store.aladdin.controllers.AdminController.warehouse;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Warehouse;
import com.store.aladdin.services.WarehouseServices;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/warehouses")
@RequiredArgsConstructor
public class GetWarehouses {

    private final WarehouseServices warehouseServices;
 
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseServices.getAllWarehouses();
            return ResponseUtil.buildResponse("Warehouses fetched successfully", true, warehouses,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Failed to fetch warehouses", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    
}
