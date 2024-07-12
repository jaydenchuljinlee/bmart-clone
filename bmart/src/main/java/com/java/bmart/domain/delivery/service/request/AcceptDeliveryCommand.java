package com.java.bmart.domain.delivery.service.request;

public record AcceptDeliveryCommand(Long deliveryId, Long riderId) {

    public static AcceptDeliveryCommand of(Long deliveryId, Long riderId) {
        return new AcceptDeliveryCommand(deliveryId, riderId);
    }
}
