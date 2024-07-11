package com.java.bmart.domain.delivery.exception;

public abstract class DeliveryException extends RuntimeException {

    protected DeliveryException(final String message) {
        super(message);
    }
}
