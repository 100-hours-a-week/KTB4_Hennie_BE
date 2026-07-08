package com.hennie.springdatajpa.auth.dto.response;

import com.hennie.springdatajpa.auth.dto.TokenInfo;
import com.hennie.springdatajpa.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto { // 로그인 성공 시 클라이언트한테 전달할 응답
    private User user;
    private TokenInfo token;

    public static LoginResponseDto of(
            User user,
            String accessToken,
            long expiresIn
    ){
        return new LoginResponseDto(
                user,
                new TokenInfo(accessToken, expiresIn)
        );
    }
}
