package com.java.bmart.domain.order.exception;

public abstract class OrderException extends RuntimeException {

    public OrderException(String message) {
        super(message);
    }
}
