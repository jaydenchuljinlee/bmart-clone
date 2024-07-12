package com.java.bmart.domain.coupon.service;

import com.java.bmart.domain.coupon.Coupon;
import com.java.bmart.domain.coupon.UserCoupon;
import com.java.bmart.domain.coupon.exception.InvalidCouponException;
import com.java.bmart.domain.coupon.exception.NotFoundCouponException;
import com.java.bmart.domain.coupon.repository.CouponRepository;
import com.java.bmart.domain.coupon.repository.UserCouponRepository;
import com.java.bmart.domain.coupon.service.request.RegisterCouponCommand;
import com.java.bmart.domain.coupon.service.request.RegisterUserCouponCommand;
import com.java.bmart.domain.coupon.service.response.FindCouponsResponse;
import com.java.bmart.domain.coupon.service.response.FindIssuedCouponsResponse;
import com.java.bmart.domain.user.User;
import com.java.bmart.domain.user.exception.NotFoundUserException;
import com.java.bmart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public FindCouponsResponse FindCoupons() {
        List<Coupon> findCoupons = couponRepository.findByEndAtGreaterThanEqual(LocalDate.now());

        return FindCouponsResponse.from(findCoupons);
    }

    @Transactional(readOnly = true)
    public FindIssuedCouponsResponse findIssuedCoupons(Long userId) {
        User findUser = findUserByUserId(userId);
        List<UserCoupon> findUserCoupons = userCouponRepository.findByUserAndIsUsedAndCouponEndAtAfter(
                findUser, false, LocalDate.now()
        );

        return FindIssuedCouponsResponse.from(findUserCoupons);
    }

    @Transactional
    public Long createCoupon(RegisterCouponCommand command) {
        Coupon coupon = Coupon.builder().name(command.name())
                .discount(command.discount())
                .description(command.description())
                .minOrderPrice(command.minOrderPrice())
                .endAt(command.endAt())
                .build();

        return couponRepository.save(coupon).getCouponId();
    }

    @Transactional
    public Long registerUserCoupon(RegisterUserCouponCommand command) {
        User findUser = findUserByUserId(command.userId());
        Coupon findCoupon = findCouponByCouponId(command.couponId());

        validateCouponExpiration(findCoupon.getEndAt());
        validateAlreadyIssuedCoupon(findUser, findCoupon);

        UserCoupon userCoupon = new UserCoupon(findUser, findCoupon);
        return userCouponRepository.save(userCoupon).getUserCouponId();
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("존재하지 않은 사용자입니다."));
    }

    private Coupon findCouponByCouponId(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundCouponException("존재하지 않은 쿠폰입니다."));
    }

    private void validateCouponExpiration(LocalDate expirationDate) {
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new InvalidCouponException("쿠폰이 이미 만료되었습니다");
        }
    }

    private void validateAlreadyIssuedCoupon(User user, Coupon coupon) {
        if (userCouponRepository.existsByUserAndCoupon(user, coupon)) {
            throw new InvalidCouponException("이미 발급받은 쿠폰입니다.");
        }
    }
}
