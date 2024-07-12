package com.java.bmart.domain.order.service.response;


import com.java.bmart.domain.coupon.Coupon;
import com.java.bmart.domain.order.Order;

public record UpdateOrderByCouponResponse(
    Integer totalPrice,
    Integer discountPrice
) {

    public static UpdateOrderByCouponResponse of(final Order order, final Coupon coupon) {
        return new UpdateOrderByCouponResponse(order.getPrice(),
            coupon.getDiscount());
    }
}
