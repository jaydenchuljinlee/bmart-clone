package com.java.bmart.domain.delivery.service.response;


import com.java.bmart.domain.delivery.Delivery;
import com.java.bmart.domain.delivery.DeliveryStatus;

import java.time.LocalDateTime;

public record FindDeliveryByOrderResponse(
    Long deliveryId,
    DeliveryStatus deliveryStatus,
    LocalDateTime createdAt,
    LocalDateTime arrivedAt,
    Long orderId,
    String orderName,
    int orderPrice,
    String riderRequest) {

    public static FindDeliveryByOrderResponse from(final Delivery delivery) {
        return new FindDeliveryByOrderResponse(
            delivery.getDeliveryId(),
            delivery.getDeliveryStatus(),
            delivery.getCreatedAt(),
            delivery.getArrivedAt(),
            delivery.getOrder().getOrderId(),
            delivery.getOrder().getName(),
            delivery.getOrderPrice(),
            delivery.getRiderRequest());
    }
}
