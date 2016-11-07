package com.restbucks.bff.wechatstore.http.jwt;

public class CannotIssueJwtException extends RuntimeException {
    public CannotIssueJwtException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
