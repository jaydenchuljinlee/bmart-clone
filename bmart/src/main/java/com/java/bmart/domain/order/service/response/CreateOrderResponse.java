package com.java.bmart.domain.order.service.response;


import com.java.bmart.domain.order.Order;

public record CreateOrderResponse(
    Long orderId,
    String name,
    Integer totalPrice,
    String address,
    Integer deliveryFee

) {

    public static CreateOrderResponse from(Order order) {
        return new CreateOrderResponse(
            order.getOrderId(),
            order.getName(),
            order.getPrice(),
            order.getAddress(),
            order.getDeliveryFee()
        );
    }
}
