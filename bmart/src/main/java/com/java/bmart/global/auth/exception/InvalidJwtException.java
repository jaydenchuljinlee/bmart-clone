package com.java.bmart.global.auth.exception;

public class InvalidJwtException extends AuthException{
    public InvalidJwtException(final String message) { super(message); }
}
