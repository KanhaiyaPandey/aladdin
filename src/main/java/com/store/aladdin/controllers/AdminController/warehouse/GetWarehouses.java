package com.store.aladdin.controllers.AdminController.warehouse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Warehouse;
import com.store.aladdin.services.WarehouseServices;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/admin/warehouses")
public class GetWarehouses {

    @Autowired
    private WarehouseServices warehouseServices;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseServices.getAllWarehouses();
            return ResponseUtil.buildResponse("Warehouses fetched successfully", HttpStatus.OK, warehouses);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Failed to fetch warehouses", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    
}
