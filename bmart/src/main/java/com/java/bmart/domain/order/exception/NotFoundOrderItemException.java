package com.java.bmart.domain.order.exception;

public class NotFoundOrderItemException extends OrderException {

    public NotFoundOrderItemException(final String message) {
        super(message);
    }
}
