package com.hennie.springdatajpa.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfo {  // 응답용
    private String accessToken;
    private long expiresIn;
}
