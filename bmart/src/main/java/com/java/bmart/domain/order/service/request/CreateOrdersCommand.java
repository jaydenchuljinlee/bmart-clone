package com.java.bmart.domain.order.service.request;


import com.java.bmart.domain.order.controller.request.CreateOrderRequest;

public record CreateOrdersCommand(
    Long userId,
    CreateOrderRequest createOrderRequest
) {

    public static CreateOrdersCommand of(final Long userId,
        final CreateOrderRequest createOrderRequest) {
        return new CreateOrdersCommand(userId, createOrderRequest);
    }
}
