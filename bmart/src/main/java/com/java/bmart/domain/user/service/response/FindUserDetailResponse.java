package com.java.bmart.domain.user.service.response;

import com.java.bmart.domain.user.User;
import com.java.bmart.domain.user.UserGrade;
import com.java.bmart.domain.user.UserRole;

public record FindUserDetailResponse(
        Long userId,
        String nickname,
        String email,
        String provider,
        String providerId,
        UserRole userRole,
        UserGrade userGrade) {

    public static FindUserDetailResponse from(final User findUser) {
        return new FindUserDetailResponse(
                findUser.getUserId(),
                findUser.getNickname(),
                findUser.getEmail(),
                findUser.getProvider(),
                findUser.getProviderId(),
                findUser.getUserRole(),
                findUser.getUserGrade());
    }
}
