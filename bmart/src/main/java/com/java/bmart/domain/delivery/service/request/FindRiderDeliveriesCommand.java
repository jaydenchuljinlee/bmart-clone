package com.java.bmart.domain.delivery.service.request;

import com.java.bmart.domain.delivery.DeliveryStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public record FindRiderDeliveriesCommand(
    Long riderId,
    List<DeliveryStatus> deliveryStatuses,
    Pageable pageable) {

    public static FindRiderDeliveriesCommand of(
        final Long riderId,
        final List<DeliveryStatus> deliveryStatuses,
        final Pageable pageable) {
        return new FindRiderDeliveriesCommand(riderId, deliveryStatuses, pageable);
    }
}
