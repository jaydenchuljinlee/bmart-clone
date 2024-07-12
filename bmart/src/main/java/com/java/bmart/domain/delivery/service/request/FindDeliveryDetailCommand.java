package com.java.bmart.domain.delivery.service.request;

public record FindDeliveryDetailCommand(Long deliveryId) {

    public static FindDeliveryDetailCommand from(final Long deliveryId) {
        return new FindDeliveryDetailCommand(deliveryId);
    }
}
