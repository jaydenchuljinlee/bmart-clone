package com.java.bmart.domain.event.exception;

public abstract class EventException extends RuntimeException {

    public EventException(final String message) {
        super(message);
    }
}
