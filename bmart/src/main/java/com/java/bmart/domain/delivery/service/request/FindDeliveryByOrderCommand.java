package com.java.bmart.domain.delivery.service.request;

public record FindDeliveryByOrderCommand(Long userId, Long orderId) {

    public static FindDeliveryByOrderCommand of(final Long userId,final Long orderId) {
        return new FindDeliveryByOrderCommand(userId, orderId);
    }
}
