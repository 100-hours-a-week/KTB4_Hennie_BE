package com.hennie.springdatajpa.global.exception;

import org.springframework.http.HttpStatus;

// 401
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
