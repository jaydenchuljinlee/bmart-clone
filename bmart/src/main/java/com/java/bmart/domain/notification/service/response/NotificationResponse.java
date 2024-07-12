package com.java.bmart.domain.notification.service.response;


import com.java.bmart.domain.notification.Notification;
import com.java.bmart.domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
    String title,
    String content,
    NotificationType notificationType,
    Long userId,
    LocalDateTime createdAt) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
            notification.getTitle(),
            notification.getContent(),
            notification.getNotificationType(),
            notification.getUserId(),
            notification.getCreatedAt());
    }
}
