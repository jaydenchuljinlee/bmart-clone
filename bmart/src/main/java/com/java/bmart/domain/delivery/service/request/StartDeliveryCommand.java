package com.java.bmart.domain.delivery.service.request;

public record StartDeliveryCommand(
    Long deliveryId,
    int deliveryEstimateMinutes,
    Long riderId) {

    public static StartDeliveryCommand of(
        final Long deliveryId,
        final Integer deliveryEstimateMinutes,
        final Long riderId) {
        return new StartDeliveryCommand(deliveryId, deliveryEstimateMinutes, riderId);
    }
}
