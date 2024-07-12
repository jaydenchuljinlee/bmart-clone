package com.java.bmart.domain.delivery.service.response;


import com.java.bmart.domain.delivery.Delivery;
import com.java.bmart.domain.delivery.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.List;

public record FindRiderDeliveriesResponse(
    List<FindRiderDeliveryResponse> deliveries,
    int page,
    long totalElements) {

    public static FindRiderDeliveriesResponse of(
        final List<Delivery> content,
        final int page,
        final long totalElements) {
        List<FindRiderDeliveryResponse> deliveries = content.stream()
            .map(FindRiderDeliveryResponse::from)
            .toList();
        return new FindRiderDeliveriesResponse(
            deliveries,
            page,
            totalElements);
    }

    public record FindRiderDeliveryResponse(
        Long deliveryId,
        DeliveryStatus deliveryStatus,
        LocalDateTime arrivedAt,
        LocalDateTime createdAt,
        String address,
        int orderPrice,
        String riderRequest,
        int deliveryFee) {

        public static FindRiderDeliveryResponse from(final Delivery delivery) {
            return new FindRiderDeliveryResponse(
                delivery.getDeliveryId(),
                delivery.getDeliveryStatus(),
                delivery.getArrivedAt(),
                delivery.getCreatedAt(),
                delivery.getAddress(),
                delivery.getOrderPrice(),
                delivery.getRiderRequest(),
                delivery.getDeliveryFee());
        }
    }
}
