package com.restbucks.bff.wechatstore.http.jwt;

public class CannotVerifyJwtException extends RuntimeException {
    public CannotVerifyJwtException(String message) {
        super(message);
    }

    public CannotVerifyJwtException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
