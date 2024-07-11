package com.java.bmart.domain.order.exception;

public class UnauthorizedOrderException extends OrderException {

    public UnauthorizedOrderException(final String message) {
        super(message);
    }
}
