package com.restbucks.bff.wechatstore.http;

public class CannotVerifyJwtException extends RuntimeException {
    public CannotVerifyJwtException(String message) {
        super(message);
    }

    public CannotVerifyJwtException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
