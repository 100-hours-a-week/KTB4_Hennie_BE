package com.hennie.springdatajpa.global.response;

import lombok.Getter;

@Getter
public class FieldErrorResponse {
    private String field;
    private String reason;

    public FieldErrorResponse(String field, String reason) {
        this.field = field;
        this.reason = reason;
    }
}
