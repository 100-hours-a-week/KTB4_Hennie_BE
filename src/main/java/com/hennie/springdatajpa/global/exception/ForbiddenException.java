package com.hennie.springdatajpa.global.exception;

import org.springframework.http.HttpStatus;

// 403
public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}