package com.java.bmart.domain.coupon.service.request;


import com.java.bmart.domain.coupon.controller.request.RegisterCouponRequest;

import java.time.LocalDate;

public record RegisterCouponCommand(
        String name,
        Integer discount,
        String description,
        Integer minOrderPrice,
        LocalDate endAt
) {

    public static RegisterCouponCommand from(RegisterCouponRequest request) {
        return new RegisterCouponCommand(
                request.name(),
                request.discount(),
                request.description(),
                request.minOrderPrice(),
                request.endAt()
        );
    }
}
