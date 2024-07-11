package com.java.bmart.global.jwt.dto;

import com.java.bmart.domain.user.UserRole;
import com.java.bmart.domain.user.service.response.RegisterUserResponse;

public record CreateTokenCommand(Long userId, UserRole userRole) {
    public static CreateTokenCommand from(RegisterUserResponse userResponse) {
        return new CreateTokenCommand(userResponse.userId(), userResponse.userRole());
    }

    public static CreateTokenCommand of(final Long userId, final UserRole userRole) {
        return new CreateTokenCommand(userId, userRole);
    }
}
