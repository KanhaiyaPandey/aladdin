package com.store.aladdin.validations;


import com.store.aladdin.dtos.orderDTOs.OrderRequestDTO;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import org.springframework.stereotype.Service;

@Service
public class OrderValidation {

    // Main entry
    public void validateIncomingOrder(OrderRequestDTO order) {

        if (order == null) {
            throw new CustomeRuntimeExceptionsHandler("Order payload is empty");
        }

        validateAddress(order);
        validateCartItems(order);
    }

    // -----------------------------
    // ADDRESS VALIDATION
    // -----------------------------
    private void validateAddress(OrderRequestDTO order) {
        if (order.getAddress() == null) {
            throw new CustomeRuntimeExceptionsHandler("Shipping address is required");
        }

        var a = order.getAddress();

        if (isBlank(a.getFirstName())) throw new CustomeRuntimeExceptionsHandler("First name required");
        if (isBlank(a.getLastName())) throw new CustomeRuntimeExceptionsHandler("Last name required");
        if (isBlank(a.getHouseNumber())) throw new CustomeRuntimeExceptionsHandler("House number required");
        if (isBlank(a.getArea())) throw new CustomeRuntimeExceptionsHandler("Area required");
        if (isBlank(a.getCity())) throw new CustomeRuntimeExceptionsHandler("City required");
        if (isBlank(a.getState())) throw new CustomeRuntimeExceptionsHandler("State required");
        if (isBlank(a.getPincode())) throw new CustomeRuntimeExceptionsHandler("Pincode required");
        if (isBlank(a.getPhoneNumber())) throw new CustomeRuntimeExceptionsHandler("Phone number required");
    }

    // -----------------------------
    // CART ITEM VALIDATION
    // -----------------------------
    private void validateCartItems(OrderRequestDTO order) {

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new CustomeRuntimeExceptionsHandler("Cart cannot be empty");
        }

        order.getItems().forEach(item -> {

            if (isBlank(item.getProductId())) {
                throw new CustomeRuntimeExceptionsHandler("productId is required for cart item");
            }

            // variantId is optional, do NOT validate blank
            if (item.getQuantity() <= 0) {
                throw new CustomeRuntimeExceptionsHandler("Quantity must be greater than zero");
            }

        });
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
