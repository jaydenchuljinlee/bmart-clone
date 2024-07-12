package com.java.bmart.domain.order.service.response;


import com.java.bmart.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;

public record FindOrderDetailResponse(
    LocalDateTime createdAt,
    Integer totalPrice,
    String status,
    List<FindOrderDetailItemResponse> orderItems
) {

    public static FindOrderDetailResponse from(final Order order) {
        final List<FindOrderDetailItemResponse> orderItems = order.getOrderItems()
            .stream()
            .map(FindOrderDetailItemResponse::from)
            .toList();
        return new FindOrderDetailResponse(
            order.getCreatedAt(),
            order.getPrice(),
            order.getStatus().toString(),
            orderItems
        );
    }
}
