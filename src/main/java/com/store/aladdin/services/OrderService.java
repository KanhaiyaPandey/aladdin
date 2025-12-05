package com.store.aladdin.services;

import com.store.aladdin.dtos.orderDTOs.AddressDTO;
import com.store.aladdin.dtos.orderDTOs.OrderItemDTO;
import com.store.aladdin.dtos.orderDTOs.OrderRequestDTO;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Order;
import com.store.aladdin.models.Product;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.helper.Enums;
import com.store.aladdin.validations.OrderValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.store.aladdin.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidation orderValidation;
    private final ProductRepository productRepository;

    public Order createOrder(OrderRequestDTO incomingOrder, String userId) {

        // 1) Validate incoming data
        orderValidation.validateIncomingOrder(incomingOrder);

        // 2) Build order shell
        Order order = new Order();
        order.setCustomerId(userId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(Enums.OrderStatus.PROCESSING);
        order.setPaymentStatus(Enums.PaymentStatus.PENDING);
        order.setPaymentMode(incomingOrder.getPaymentMethod());
        order.setShippingAddress(mapAddress(incomingOrder.getAddress()));

        // ---------------------------------------------------
        // 🚀 OPTIMIZATION: Fetch ALL products in one DB call
        // ---------------------------------------------------
        List<String> productIds = incomingOrder.getItems().stream()
                .map(OrderItemDTO::getProductId)
                .distinct()
                .toList();

        Map<String, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        // ---------------------------------------------------
        // 🚀 Map all order items IN-MEMORY (no DB hits)
        // ---------------------------------------------------
        List<Order.OrderItem> finalItems = incomingOrder.getItems().stream()
                .map(dto -> mapCartItem(dto, productMap))
                .toList();

        order.setItems(finalItems);

        // 4) Totals
        double subtotal = finalItems.stream()
                .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                .sum();

        order.setGrandTotal(subtotal);
        order.setExtraCharges(0.0);
        order.setShippingCharges(0.0);
        order.setDiscountAmount(0.0);
        order.setGatewayDiscount(0.0);

        // 5) Timeline
        Order.Timeline timeline = new Order.Timeline();
        timeline.setStatus(Enums.OrderStatus.PENDING);
        timeline.setTime(LocalDateTime.now());
        order.setTimeline(List.of(timeline));

        // 6) Save
        return orderRepository.save(order);
    }

    // ⭐ Uses productMap (O(1) lookup) instead of DB calls
    private Order.OrderItem mapCartItem(OrderItemDTO dto, Map<String, Product> productMap) {

        Product product = productMap.get(dto.getProductId());

        if (product == null) {
            throw new CustomeRuntimeExceptionsHandler("Product not found");
        }

        double basePrice;

        if (dto.getVariantId() == null || dto.getVariantId().trim().isEmpty()) {
            basePrice = product.getSellPrice();
        } else {
            Product.Variant variant = product.getVariants().stream()
                    .filter(v -> v.getVariantId().equals(dto.getVariantId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomeRuntimeExceptionsHandler("Variant not found"));
            basePrice = variant.getSellPrice();
        }

        Order.OrderItem item = new Order.OrderItem();
        item.setProductId(dto.getProductId());
        item.setVariantId(dto.getVariantId());
        item.setQuantity(dto.getQuantity());
        item.setPriceSnapshot(basePrice);

        return item;
    }

    private Order.Address mapAddress(AddressDTO addressDTO) {
        Order.Address a = new Order.Address();
        a.setFirstName(addressDTO.getFirstName());
        a.setLastName(addressDTO.getLastName());
        a.setHouseNumber(addressDTO.getHouseNumber());
        a.setArea(addressDTO.getArea());
        a.setCity(addressDTO.getCity());
        a.setState(addressDTO.getState());
        a.setPincode(addressDTO.getPincode());
        a.setEmail(addressDTO.getEmail());
        a.setPhoneNumber(addressDTO.getPhoneNumber());
        return a;
    }
}
