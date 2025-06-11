package com.store.aladdin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.store.aladdin.models.Order;
import com.store.aladdin.utils.helper.Enums.OrderStatus;
import com.store.aladdin.repository.OrderRepository;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    

    public List<Order> updateOrderStatus(List<String> orderIds, String status) {

        
        List<ObjectId> objectIdList = orderIds.stream()
                .map(ObjectId::new)
                .collect(Collectors.toList());


        List<Order> orders = orderRepository.findAllById(objectIdList);
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        for (Order order : orders) {
            order.setStatus(orderStatus);
        }
        return orderRepository.saveAll(orders);
    }


    public void updateShippingInfo(Map<String, Object> dataMap){

        try {

                if (dataMap == null) return;
                String channelOrderId = (String) dataMap.get("channel_order_id");
                ObjectId orderId = new ObjectId(channelOrderId);
                Optional<Order> optionalOrder = orderRepository.findById(orderId);
                if (optionalOrder.isEmpty()) {
                    System.out.println("Order not found for orderId: " + channelOrderId);
                    return;
                }
                Order order = optionalOrder.get();
                Order.ShippingDetails shippingDetails = new Order.ShippingDetails();
                shippingDetails.setOrder_id(String.valueOf(dataMap.get("order_id")));
                shippingDetails.setShipment_id(String.valueOf(dataMap.get("shipment_id")));
                shippingDetails.setStatus((String) dataMap.get("status"));
                shippingDetails.setAwb_code((String) dataMap.get("awb_code"));
                shippingDetails.setCourier_name((String) dataMap.get("courier_name"));
                shippingDetails.setPackaging_box_error((String) dataMap.get("packaging_box_error"));

                order.setShippingDetails(shippingDetails);
                orderRepository.save(order);
            
        } catch (Exception e) {
           e.printStackTrace(); 
        }
    }

}
