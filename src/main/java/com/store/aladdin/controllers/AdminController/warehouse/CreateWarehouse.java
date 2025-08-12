package com.store.aladdin.controllers.AdminController.warehouse;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.DTOs.WarehouseDTO;
import com.store.aladdin.models.Warehouse;
import com.store.aladdin.services.WarehouseServices;
import com.store.aladdin.utils.response.ResponseUtil;

@RestController
@RequestMapping("/api/admin/warehouses")
public class CreateWarehouse {

    @Autowired
    private WarehouseServices warehouseServices;

    

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO, BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseUtil.buildErrorResponse("Validation failed", HttpStatus.BAD_REQUEST, errors);
        }

        try {
            if (warehouseServices.existsByName(warehouseDTO.getName())) {
                return ResponseUtil.buildErrorResponse("Warehouse name already exists", HttpStatus.CONFLICT, null);
            }

            Warehouse createdWarehouse = warehouseServices.createWarehouse(warehouseDTO);
            return ResponseUtil.buildResponse("Warehouse created successfully", true, createdWarehouse, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Failed to create warehouse", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
}
