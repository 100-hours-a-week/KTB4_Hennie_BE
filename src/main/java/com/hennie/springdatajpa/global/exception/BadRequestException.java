package com.hennie.springdatajpa.global.exception;

import org.springframework.http.HttpStatus;

// 400
public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
