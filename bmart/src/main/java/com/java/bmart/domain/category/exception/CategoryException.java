package com.java.bmart.domain.category.exception;

public abstract class CategoryException extends RuntimeException {

    public CategoryException(final String message) {
        super(message);
    }
}
