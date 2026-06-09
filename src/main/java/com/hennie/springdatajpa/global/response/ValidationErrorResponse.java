package com.hennie.springdatajpa.global.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationErrorResponse {
    private List<FieldErrorResponse> errors;

    public ValidationErrorResponse(List<FieldErrorResponse> errors) {
        this.errors = errors;
    }
}
