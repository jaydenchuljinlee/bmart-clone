package com.java.bmart.domain.order.exception;

public class NotFoundOrderException extends OrderException {

    public NotFoundOrderException(final String message) {
        super(message);
    }
}
