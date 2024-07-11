package com.java.bmart.domain.user.exception;

public abstract class UserException extends RuntimeException {
    protected UserException(final String message) { super(message); }
}
