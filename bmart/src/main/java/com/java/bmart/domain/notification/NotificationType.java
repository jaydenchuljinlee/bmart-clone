package com.java.bmart.domain.notification;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    CONNECT("connect"),
    DELIVERY("delivery");

    private final String value;
}