package com.java.bmart.domain.user.service.request;

import com.java.bmart.domain.user.UserGrade;
import com.java.bmart.domain.user.UserRole;
import lombok.Builder;

@Builder
public record RegisterUserCommand(
        String nickname,
        String email,
        String provider,
        String providerId,
        UserRole userRole,
        UserGrade userGrade) {

    public static RegisterUserCommand of(
            final String nickname,
            final String email,
            final String provider,
            final String providerId,
            final UserRole userRole,
            final UserGrade userGrade) {
        return RegisterUserCommand.builder()
                .nickname(nickname)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .userRole(userRole)
                .userGrade(userGrade)
                .build();
    }
}
