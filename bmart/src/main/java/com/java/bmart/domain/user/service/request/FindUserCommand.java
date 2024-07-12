package com.java.bmart.domain.user.service.request;

public record FindUserCommand(Long userId) {

    public static FindUserCommand from(Long userId) {
        return new FindUserCommand(userId);
    }
}