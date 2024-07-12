package com.java.bmart.domain.coupon.service.request;

import com.java.bmart.domain.coupon.controller.request.RegisterCouponRequest;

import java.time.LocalDate;


public record RegisterUserCouponCommand(
        Long userId,
        Long couponId
) {

    public static RegisterUserCouponCommand of(
            final Long userId,
            final Long couponId) {
        return new RegisterUserCouponCommand(userId, couponId);
    }
}
