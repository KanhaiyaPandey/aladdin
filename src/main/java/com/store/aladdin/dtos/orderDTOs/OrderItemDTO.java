package com.store.aladdin.dtos.orderDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {

    @NotBlank(message = "Product ID is required")
    private String productId;

    private String variantId;

    private String media;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

}
