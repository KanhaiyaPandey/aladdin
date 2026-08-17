package com.store.aladdin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.store.aladdin.models.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.store.aladdin.dtos.WarehouseDTO;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Warehouse;
import com.store.aladdin.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServices {

    private final WarehouseRepository warehouseRepository;
    private final MongoTemplate mongoTemplate;

    private static final int MAX_UPSERT_ATTEMPTS = 5;

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
                    upsertProductStock(variantWarehouse.getWarehouseId(), sku, variantWarehouse.getStock());
                }
            }
        }
        // CASE 2: No variants -> use product-level warehouseData
        else if (product.getWarehouseData() != null && !product.getWarehouseData().isEmpty()) {
            String productSku = product.getSku();
            for (Product.Warehouse warehouseData : product.getWarehouseData()) {
                upsertProductStock(warehouseData.getWarehouseId(), productSku, warehouseData.getStock());
            }
        }
    }

    /**
     * Atomically adds `stockToAdd` to a warehouse's stock row for `sku`,
     * creating the row if it doesn't exist yet.
     *
     * Replaces a previous implementation that loaded the whole Warehouse
     * document into memory, mutated it, and saved it back inside a
     * `synchronized(warehouse)` block - since findById() returns a fresh
     * object every call, that block synchronized on a different monitor per
     * thread and provided no real mutual exclusion between concurrent
     * callers. Confirmed the damage: seeding 50 products back-to-back
     * silently dropped ~44% of the stock rows they should have created.
     *
     * Both branches below are single atomic MongoDB operations, so
     * correctness comes from MongoDB's own per-document write serialization,
     * not JVM-level locking. The retry loop only exists to handle the rare,
     * harmless case where two concurrent calls both try to *create* the same
     * row at once: one "create" wins, the other's guard condition then fails
     * to match, and it retries as an "increment" against the row the other
     * just created.
     */
    private void upsertProductStock(String warehouseId, String sku, Integer stockToAdd) {
        int stock = stockToAdd != null ? stockToAdd : 0;

        for (int attempt = 0; attempt < MAX_UPSERT_ATTEMPTS; attempt++) {
            // Row already exists for this SKU -> atomic increment in place.
            Query incrementQuery = Query.query(
                    Criteria.where("_id").is(warehouseId).and("productStocks.sku").is(sku));
            Update incrementUpdate = new Update()
                    .inc("productStocks.$.totalStock", stock)
                    .set("productStocks.$.updatedAt", LocalDateTime.now())
                    .set("updatedAt", LocalDateTime.now());
            if (mongoTemplate.updateFirst(incrementQuery, incrementUpdate, Warehouse.class).getMatchedCount() > 0) {
                return;
            }

            // No row for this SKU yet -> atomically push a new one. The
            // "$ne" guard matches only if NO element in the array has this
            // sku, so it can't double-push against a concurrent attempt -
            // the guard and the push happen in the same atomic operation.
            Warehouse.ProductStock newStock = new Warehouse.ProductStock();
            newStock.setSku(sku);
            newStock.setTotalStock(stock);
            newStock.setCommited(0);
            newStock.setDamaged(0);
            newStock.setUpdatedAt(LocalDateTime.now());

            Query pushQuery = Query.query(
                    Criteria.where("_id").is(warehouseId).and("productStocks.sku").ne(sku));
            Update pushUpdate = new Update()
                    .push("productStocks", newStock)
                    .set("updatedAt", LocalDateTime.now());
            if (mongoTemplate.updateFirst(pushQuery, pushUpdate, Warehouse.class).getMatchedCount() > 0) {
                return;
            }
            // Neither matched: a concurrent call just created/updated this
            // row between our two attempts above - loop and try the
            // increment again against the now-current state.
        }

        log.warn("Failed to upsert stock for sku {} in warehouse {} after {} attempts",
                sku, warehouseId, MAX_UPSERT_ATTEMPTS);
    }
}
