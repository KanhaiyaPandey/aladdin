package com.store.aladdin.controllers.AdminController.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.Warehouse;
import com.store.aladdin.services.WarehouseServices;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/admin/warehouses")
public class CreateWarehouse {

    @Autowired
    private WarehouseServices warehouseServices;

    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createWarehouse(@RequestBody Warehouse warehouse) {
        try {
            Warehouse createdWarehouse = warehouseServices.createWarehouse(warehouse);
            return ResponseUtil.buildResponse("Warehouse created successfully", HttpStatus.CREATED, createdWarehouse);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Failed to create warehouse", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
}
