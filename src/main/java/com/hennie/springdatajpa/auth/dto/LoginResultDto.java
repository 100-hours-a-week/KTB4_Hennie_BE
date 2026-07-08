package com.hennie.springdatajpa.auth.dto;

import com.hennie.springdatajpa.auth.dto.response.LoginResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResultDto { // 로그인 처리 결과를 두가지 전달 경로로 분리
    private LoginResponseDto response; // 응답 바디용
    private String refreshToken; // 쿠키용
}
