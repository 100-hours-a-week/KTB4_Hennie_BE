package com.hennie.springdatajpa.global.exception;

import org.springframework.http.HttpStatus;

// 404
public class NotFoundException extends BusinessException {
    public NotFoundException(String code) {
        super(code, HttpStatus.NOT_FOUND);
    }
}