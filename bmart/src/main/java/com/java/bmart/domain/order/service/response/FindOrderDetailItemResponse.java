package com.java.bmart.domain.order.service.response;


import com.java.bmart.domain.order.OrderItem;

public record FindOrderDetailItemResponse(
    Long itemId,
    String name,
    Integer quantity,
    Integer price
) {

    public static FindOrderDetailItemResponse from(final OrderItem orderItem) {
        return new FindOrderDetailItemResponse(
            orderItem.getItem().getItemId(),
            orderItem.getItem().getName(),
            orderItem.getQuantity(),
            orderItem.getItem().getPrice()
        );
    }
}
