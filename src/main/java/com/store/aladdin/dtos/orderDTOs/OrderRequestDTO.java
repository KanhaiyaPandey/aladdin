package com.store.aladdin.dtos.orderDTOs;

import com.store.aladdin.utils.helper.Enums;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotEmpty(message = "Order must contain at least one item")
    private List<@Valid OrderItemDTO> items;

    @NotBlank(message = "Payment method is required")
    private Enums.PaymentMode paymentMethod;

    @NotBlank(message = "Address is required")
    private AddressDTO address;

}
