package com.hennie.springdatajpa.global.exception;

import org.springframework.http.HttpStatus;

// 401
public class AuthorizedException extends BusinessException {
    public AuthorizedException(String code) {
        super(code, HttpStatus.UNAUTHORIZED);
    }
}
