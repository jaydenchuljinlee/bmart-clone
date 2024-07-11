package com.java.bmart.domain.item.exception;


public abstract class ItemException extends RuntimeException {

    public ItemException(final String message) {
        super(message);
    }
}

