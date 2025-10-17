package com.store.aladdin.controllers.admincontroller.warehouse;

import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.dtos.WarehouseDTO;
import com.store.aladdin.models.Warehouse;
import com.store.aladdin.services.WarehouseServices;
import com.store.aladdin.utils.response.ResponseUtil;

import lombok.RequiredArgsConstructor;

import static com.store.aladdin.routes.WarehouseRoutes.*;

@RestController
@RequestMapping(WAREHOUSE_BASE)
@RequiredArgsConstructor
public class CreateWarehouse {

    private final WarehouseServices warehouseServices;

    @PostMapping(CREATE_WAREHOUSE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO, BindingResult result) {
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
