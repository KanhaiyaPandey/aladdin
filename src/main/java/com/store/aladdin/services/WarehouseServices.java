package com.store.aladdin.services;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.store.aladdin.dtos.WarehouseDTO;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Warehouse;
import com.store.aladdin.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseServices {

    private final WarehouseRepository warehouseRepository;

    // Create a new Warehouse
    public Warehouse createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseDTO.getName());
        warehouse.setAddress(warehouseDTO.getAddress());
        warehouse.setPincode(warehouseDTO.getPincode());

        return warehouseRepository.save(warehouse);
    }

    // Get a Warehouse by ID
    public Optional<Warehouse> getWarehouseById(ObjectId id) {
        return warehouseRepository.findById(id);
    }

    // Get all Warehouses
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    // Update a Warehouse
    public Warehouse updateWarehouse(ObjectId id, Warehouse updatedWarehouse) {
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setName(updatedWarehouse.getName());
            warehouse.setAddress(updatedWarehouse.getAddress());
            warehouse.setPincode(updatedWarehouse.getPincode());
            return warehouseRepository.save(warehouse);
        }).orElseThrow(() -> new CustomeRuntimeExceptionsHandler("Warehouse not found with id: " + id));
    }

    // Delete a Warehouse
    public void deleteWarehouse(ObjectId id) {
        warehouseRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
    return warehouseRepository.existsByNameIgnoreCase(name);
}

    
}
