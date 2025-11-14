package com.store.aladdin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.store.aladdin.models.Product;
import org.springframework.scheduling.annotation.Async;
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
        warehouse.setCreatedAt(LocalDateTime.now());
        warehouse.setUpdatedAt(LocalDateTime.now());

        return warehouseRepository.save(warehouse);
    }

    // Get a Warehouse by ID
    public Optional<Warehouse> getWarehouseById(String id) {
        return warehouseRepository.findById(id);
    }

    // Get all Warehouses
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    // Update a Warehouse
    public Warehouse updateWarehouse(String id, Warehouse updatedWarehouse) {
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setName(updatedWarehouse.getName());
            warehouse.setAddress(updatedWarehouse.getAddress());
            warehouse.setPincode(updatedWarehouse.getPincode());
            return warehouseRepository.save(warehouse);
        }).orElseThrow(() -> new CustomeRuntimeExceptionsHandler("Warehouse not found with id: " + id));
    }

    // Delete a Warehouse
    public void deleteWarehouse(String id) {

        warehouseRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
    return warehouseRepository.existsByNameIgnoreCase(name);
}


    @Async
    public void createStockAsync(Product product) {
        // CASE 1: Product has variants
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (Product.Variant variant : product.getVariants()) {
                String sku = variant.getVariantSku();

                for (Product.Warehouse variantWarehouse : variant.getVariantWarehouseData()) {
                    warehouseRepository.findById(variantWarehouse.getWarehouseId()).ifPresent(warehouse -> {
                        synchronized (warehouse) { // ensure thread safety for updates on the same warehouse
                            Warehouse.ProductStock existingStock = warehouse.getProductStocks().stream()
                                    .filter(s -> s.getSku().equals(sku))
                                    .findFirst()
                                    .orElse(null);

                            if (existingStock != null) {
                                existingStock.setTotalStock(
                                        (existingStock.getTotalStock() != null ? existingStock.getTotalStock() : 0)
                                                + (variantWarehouse.getStock() != null ? variantWarehouse.getStock() : 0)
                                );
                                existingStock.setUpdatedAt(LocalDateTime.now());
                            } else {
                                Warehouse.ProductStock newStock = new Warehouse.ProductStock();
                                newStock.setSku(sku);
                                newStock.setTotalStock(variantWarehouse.getStock());
                                newStock.setCommited(0);
                                newStock.setDamaged(0);
                                newStock.setUpdatedAt(LocalDateTime.now());
                                warehouse.getProductStocks().add(newStock);
                            }

                            warehouse.setUpdatedAt(LocalDateTime.now());
                            warehouseRepository.save(warehouse);
                        }
                    });
                }
            }
        }
        // CASE 2: No variants → use product-level warehouseData
        else if (product.getWarehouseData() != null && !product.getWarehouseData().isEmpty()) {
            String productSku = product.getSku(); // assuming your product has a SKU field
            for (Product.Warehouse warehouseData : product.getWarehouseData()) {
                warehouseRepository.findById(warehouseData.getWarehouseId()).ifPresent(warehouse -> {
                    synchronized (warehouse) {
                        Warehouse.ProductStock existingStock = warehouse.getProductStocks().stream()
                                .filter(s -> s.getSku().equals(productSku))
                                .findFirst()
                                .orElse(null);

                        if (existingStock != null) {
                            existingStock.setTotalStock(
                                    (existingStock.getTotalStock() != null ? existingStock.getTotalStock() : 0)
                                            + (warehouseData.getStock() != null ? warehouseData.getStock() : 0)
                            );
                            existingStock.setUpdatedAt(LocalDateTime.now());
                        } else {
                            Warehouse.ProductStock newStock = new Warehouse.ProductStock();
                            newStock.setSku(productSku);
                            newStock.setTotalStock(warehouseData.getStock());
                            newStock.setCommited(0);
                            newStock.setDamaged(0);
                            newStock.setUpdatedAt(LocalDateTime.now());
                            warehouse.getProductStocks().add(newStock);
                        }

                        warehouse.setUpdatedAt(LocalDateTime.now());
                        warehouseRepository.save(warehouse);
                    }
                });
            }
        }
    }

    
}
