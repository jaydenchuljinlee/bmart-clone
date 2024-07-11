package com.java.bmart.domain.notification.exception;

public abstract class NotificationException extends RuntimeException {

    public NotificationException(String message) {
        super(message);
    }
}
