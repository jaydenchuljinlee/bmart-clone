package com.java.bmart.domain.event.exception;

public abstract class EventItemException extends RuntimeException {

    public EventItemException(String message) {
        super(message);
    }
}
