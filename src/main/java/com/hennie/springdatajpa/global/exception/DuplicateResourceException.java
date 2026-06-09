package com.hennie.springdatajpa.global.exception;

import org.springframework.http.HttpStatus;

// 409
public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String code) {
        super(code, HttpStatus.CONFLICT);
    }
}
