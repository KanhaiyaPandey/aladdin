package com.store.aladdin.models;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@Document(collection = "warehouse")
@NoArgsConstructor
public class Warehouse {

    @Id
    private String warehouseId;

    @NotBlank(message = "Warehouse name is required")
    @Size(min = 3, max = 100, message = "Warehouse name must be between 3 and 100 characters")
    @Indexed(unique = true)
    private String name;

    @NotBlank(message = "Address is required")
    @Size(min = 5, message = "Address must be at least 5 characters long")
    private String address;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9]\\d{5}$", message = "Pincode must be a valid 6-digit Indian pincode")
    private String pincode;

    private List<ProductStock> productStocks = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    public static class ProductStock{
        private String sku;
        private String category;
        private Integer totalStock;
        private Integer commited;
        private Integer damaged;
        private LocalDateTime updatedAt;
    }

}
